"""
Nano Banana / Imagen 3 Automated Generator for Rosarium Holy Mysteries.
Generates all 20 mystery images with character consistency and saves them
directly into app/src/main/res/drawable-nodpi/.

Usage:
    export GEMINI_API_KEY="your_api_key_here"
    python scripts/generate_mysteries_nano_banana.py
"""

import os
import sys
import json
from pathlib import Path

# Character Anchors for Character Consistency
MARY_ANCHOR_YOUNG = (
    "A serene young Galilean woman of 16-18 years, almond-shaped hazel-brown eyes, "
    "gentle compassionate expression, straight delicate nose, fair Mediterranean complexion. "
    "Draped in an iconic deep lapis-lazuli ultramarine mantle with subtle gold-embroidered hem "
    "over a soft rose-madder red tunic. Sheer ivory linen veil under the mantle over parted wavy "
    "chestnut-brown hair. A glowing circular gold-leaf halo with delicate Gothic geometric filigree rim."
)

MARY_ANCHOR_MATURE = (
    "The same Galilean woman in her 40s to 50s, maintaining the exact facial bone structure, "
    "hazel-brown eyes, and noble posture, but imbued with profound contemplative sorrow and maternal dignity. "
    "Wearing the same deep lapis-lazuli ultramarine mantle, dark navy-blue mourning veil over graying "
    "chestnut hair, and radiant sacred gold-leaf halo."
)

JESUS_ANCHOR = (
    "A noble Semitic man in his early 30s, piercing yet profoundly merciful warm amber-brown eyes, "
    "shoulder-length wavy dark chestnut-brown hair parted down the center, neatly groomed soft beard and mustache, "
    "high cheekbones, serene and divine expression. Wearing a seamless unbleached off-white linen tunic with a "
    "deep oxblood/crimson draped mantle. A radiant circular cruciform gold-leaf halo emitting soft celestial rays."
)

STYLE_SUFFIX = (
    "Sacred Renaissance oil painting with illuminated medieval manuscript gilding, in the master style of "
    "Fra Angelico and Leonardo da Vinci. Dramatic chiaroscuro lighting, warm candlelit amber undertones, "
    "rich ultramarine blue and oxblood red pigments, antique gold leaf gilding. 1:1 square aspect ratio, "
    "circular centered medallion composition, soft atmospheric depth, reverent Catholic sacred art, 4k ultra-detailed canvas texture."
)

MYSTERIES = [
    # Joyful
    ("mystery_joyful_1", f"The Annunciation. Inside a quiet Nazareth stone chamber. {MARY_ANCHOR_YOUNG} kneels reverently before an ornate wooden prie-dieu. Before her stands Archangel Gabriel, a majestic celestial youth with iridescent feathered wings, clad in white and gold liturgical vestments, holding a stem of pure white lilies. A heavenly beam of golden light descends carrying a radiant golden dove. {STYLE_SUFFIX}"),
    ("mystery_joyful_2", f"The Visitation. At the stone doorway of Zechariah's home in the Judean hill country. {MARY_ANCHOR_YOUNG} warmly embraces her elderly cousin Elizabeth, who wears a white head-wrap and bows with tears of joy. Rolling green vineyard terraces in background. Golden divine aura surrounds both women. {STYLE_SUFFIX}"),
    ("mystery_joyful_3", f"The Nativity of Our Lord. Inside a rustic stone grotto in Bethlehem at night. In a wooden manger filled with golden straw lies the newborn infant Jesus Christ, radiating soft warm divine light. {MARY_ANCHOR_YOUNG} kneels adoringly over her Child. Beside her stands Saint Joseph in a warm brown cloak with traveler's staff. An ox and donkey rest in the soft shadows. A brilliant golden celestial star shines above. {STYLE_SUFFIX}"),
    ("mystery_joyful_4", f"The Presentation in the Temple. Inside the grand Jerusalem Temple with fluted marble columns. The elderly, white-bearded priest Simeon in embroidered liturgical vestments holds the infant Jesus Christ, lifting Him toward heaven. {MARY_ANCHOR_YOUNG} stands nearby in humble adoration. Saint Joseph holds a basket of two white turtle doves. {STYLE_SUFFIX}"),
    ("mystery_joyful_5", f"The Finding in the Temple. In an illuminated stone portico of the Jerusalem Temple. A twelve-year-old boy Jesus, sharing the youthful facial features of {JESUS_ANCHOR}, sits calmly among venerable seated Jewish rabbis holding Torah scrolls, who listen in astonishment. In the background, {MARY_ANCHOR_YOUNG} and Joseph arrive with immense relief and wonder. {STYLE_SUFFIX}"),

    # Luminous
    ("mystery_luminous_1", f"The Baptism of Jesus. Standing in the flowing waters of the Jordan River at dawn. {JESUS_ANCHOR} stands with hands folded in humility. Saint John the Baptist pours river water over Christ's head from a scallop shell. Directly above Christ's head, the heavens part in radiant golden celestial glory, with the Holy Spirit descending as a luminous white dove. {STYLE_SUFFIX}"),
    ("mystery_luminous_2", f"The Wedding Feast at Cana. In an ornate festive banquet hall with stone arches. {MARY_ANCHOR_YOUNG} leans toward {JESUS_ANCHOR}, speaking gently with serene faith. Jesus extends His right hand in quiet blessing over six large stone water jars as servants pour clear water turning into rich, glowing red wine. {STYLE_SUFFIX}"),
    ("mystery_luminous_3", f"The Proclamation of the Kingdom of God. On a windswept green hillside overlooking the Sea of Galilee. {JESUS_ANCHOR} stands elevated on a stone ledge, right hand raised in benediction as He preaches the Beatitudes. A diverse, reverent crowd of disciples and families listen intently. {STYLE_SUFFIX}"),
    ("mystery_luminous_4", f"The Transfiguration on Mount Tabor. {JESUS_ANCHOR} stands atop a mountain peak, His garments dazzling brilliant radiant white as the sun, face glowing with transcendent celestial majesty. Flanking Him are Moses with stone tablets and Elijah in prophetic robes. Below, Apostles Peter, James, and John shield their eyes in overwhelming awe. {STYLE_SUFFIX}"),
    ("mystery_luminous_5", f"The Institution of the Holy Eucharist at the Last Supper. In the warm candlelit Upper Room. {JESUS_ANCHOR} sits at the center of the wooden table, elevating a round golden unleavened host in His hands, eyes lifted to heaven. Before Him rests a golden chalice of red wine. Around Him, the Twelve Apostles lean forward in solemn reverence. {STYLE_SUFFIX}"),

    # Sorrowful
    ("mystery_sorrowful_1", f"The Agony in the Garden of Gethsemane. Under twisted, ancient olive trees beneath a dark night sky. {JESUS_ANCHOR} is prostrate in agonized prayer on a mossy rock, hands clasped tightly, gazing toward heaven in surrender. Beads of sweat like drops of blood fall to the ground. A comforting celestial angel in soft pearl light offers a golden cup of consolation. {STYLE_SUFFIX}"),
    ("mystery_sorrowful_2", f"The Scourging at the Pillar. In the dark stone courtyard of Pontius Pilate's praetorium. {JESUS_ANCHOR} is bound with heavy ropes to a central fluted Roman marble pillar, bearing red marks of the scourge, enduring the torment with profound divine meekness, compassion, and silent redemptive love. Torches cast dramatic shadows. {STYLE_SUFFIX}"),
    ("mystery_sorrowful_3", f"The Crowning with Thorns. Inside Roman barracks. {JESUS_ANCHOR} is mockingly seated on a stone bench in a faded scarlet robe. A rough crown woven of thick desert thorns is pressed upon His brow with drops of blood trickling down. In His bound hands He holds a fragile reed scepter, gazing downward with boundless mercy and dignity. {STYLE_SUFFIX}"),
    ("mystery_sorrowful_4", f"The Carrying of the Cross. On the cobblestone streets of Jerusalem leading to Golgotha. {JESUS_ANCHOR} carries the massive rough-hewn wooden Cross upon His bleeding shoulder. In the crowd, He locks eyes with {MARY_ANCHOR_MATURE}, whose face is filled with unfathomable maternal anguish. Simon of Cyrene assists in carrying the Cross. {STYLE_SUFFIX}"),
    ("mystery_sorrowful_5", f"The Crucifixion of Jesus Christ on Mount Calvary. Under a darkened stormy sky with breaks of golden-gray light. {JESUS_ANCHOR} hangs upon the towering wooden Cross. At the foot of the Cross stands {MARY_ANCHOR_MATURE} in deep, resolute sorrow, supported by Saint John. Mary Magdalene kneels clutching the base of the cross in weeping devotion. {STYLE_SUFFIX}"),

    # Glorious
    ("mystery_glorious_1", f"The Resurrection of Jesus Christ. At the dawn of the third day, the golden light of the rising sun breaking over Jerusalem. {JESUS_ANCHOR} emerges triumphant and glorious from the open rock-hewn tomb, clothed in dazzling white robes, holding the red-cross white banner of victory over death. Two white-robed angels kneel in adoration. {STYLE_SUFFIX}"),
    ("mystery_glorious_2", f"The Ascension of Our Lord into Heaven. Atop the Mount of Olives under a luminous azure sky. {JESUS_ANCHOR} ascends majestically into radiant golden and amber clouds, right hand raised in perpetual blessing. Below, {MARY_ANCHOR_MATURE} and the Eleven Apostles gaze upward in holy wonder. {STYLE_SUFFIX}"),
    ("mystery_glorious_3", f"The Descent of the Holy Spirit at Pentecost. Inside the Cenacle Upper Room. {MARY_ANCHOR_MATURE} sits at the center in peaceful prayer, surrounded by the Twelve Apostles. Above each of their heads rests a dancing tongue of sacred fire. From above, the Holy Spirit appears as a brilliant golden dove emitting divine beams of light. {STYLE_SUFFIX}"),
    ("mystery_glorious_4", f"The Assumption of the Blessed Virgin Mary into Heaven. {MARY_ANCHOR_MATURE} (radiant with eternal youth and celestial grace) is borne bodily upward into heavenly clouds by a host of golden seraphim. Below on earth, the Apostles gather around her empty stone tomb filled with fragrant white lilies and roses in awe. {STYLE_SUFFIX}"),
    ("mystery_glorious_5", f"The Coronation of Mary as Queen of Heaven. In the highest celestial court of Heaven surrounded by singing angels. {MARY_ANCHOR_MATURE}, enthroned in radiant glory, bows her head humbly as {JESUS_ANCHOR} places an ornate crown of twelve brilliant stars and pure gold upon her head. Divine golden light, deep ultramarine, and celestial crimson majesty. {STYLE_SUFFIX}"),
]

def main():
    dest_dir = Path("app/src/main/res/drawable-nodpi")
    dest_dir.mkdir(parents=True, exist_ok=True)
    
    print(f"=== Nano Banana / Imagen 3 Sacred Mystery Generator ===")
    print(f"Total Mysteries to generate: {len(MYSTERIES)}")
    print(f"Target Directory: {dest_dir.resolve()}\n")

    api_key = os.environ.get("GEMINI_API_KEY") or os.environ.get("GOOGLE_API_KEY")
    if not api_key:
        print("Note: GEMINI_API_KEY environment variable is not set.")
        print("You can generate the images in Google AI Studio, Gemini, or any Nano Banana UI using the prompts in:")
        print(" -> NANO_BANANA_MYSTERIES_PROMPTS.md")
        print("\nAll 20 prompts are also exported below to 'nano_banana_prompts.json'.")
        
        # Export prompts to JSON
        output_file = Path("scripts/nano_banana_prompts.json")
        with open(output_file, "w", encoding="utf-8") as f:
            json.dump([{"filename": f"{name}.png", "prompt": p} for name, p in MYSTERIES], f, indent=2)
        print(f"Exported to {output_file.resolve()}.")
        return

    try:
        from google import genai
        client = genai.Client(api_key=api_key)
        for idx, (filename, prompt) in enumerate(MYSTERIES, 1):
            out_path = dest_dir / f"{filename}.png"
            if out_path.exists():
                print(f"[{idx}/20] {filename}.png already exists. Skipping.")
                continue

            print(f"[{idx}/20] Generating {filename}.png with Nano Banana (imagen-3.0-generate-002)...")
            result = client.models.generate_images(
                model="imagen-3.0-generate-002",
                prompt=prompt,
                config=dict(
                    number_of_images=1,
                    aspect_ratio="1:1",
                    person_generation="ALLOW_ADULT",
                    safety_filter_level="BLOCK_MEDIUM_AND_ABOVE",
                ),
            )
            for generated_image in result.generated_images:
                with open(out_path, "wb") as f:
                    f.write(generated_image.image.image_bytes)
                print(f"       -> Saved {out_path}")
        print("\nAll 20 Holy Mystery images generated successfully!")
    except ImportError:
        print("google-genai SDK not installed. Run: pip install google-genai")
    except Exception as e:
        print(f"Error during generation: {e}")

if __name__ == "__main__":
    main()
