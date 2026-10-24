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
import base64
import urllib.request
import urllib.error
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

def generate_with_nano_banana(api_key: str, model_name: str, prompt: str) -> bytes:
    """Invokes Google Gemini Nano Banana image generation via REST API."""
    url = f"https://generativelanguage.googleapis.com/v1beta/models/{model_name}:generateContent?key={api_key}"
    payload = {
        "contents": [
            {
                "parts": [
                    {"text": prompt}
                ]
            }
        ]
    }
    data = json.dumps(payload).encode("utf-8")
    req = urllib.request.Request(url, data=data, headers={"Content-Type": "application/json"})
    
    with urllib.request.urlopen(req) as resp:
        res_json = json.loads(resp.read().decode("utf-8"))
        candidates = res_json.get("candidates", [])
        if not candidates:
            raise RuntimeError(f"No candidates returned: {res_json}")
        
        parts = candidates[0].get("content", {}).get("parts", [])
        for part in parts:
            if "inlineData" in part:
                b64_data = part["inlineData"]["data"]
                return base64.b64decode(b64_data)
        raise RuntimeError("No image inlineData in response.")

def main():
    dest_dir = Path("app/src/main/res/drawable-nodpi")
    dest_dir.mkdir(parents=True, exist_ok=True)
    
    api_key = sys.argv[1] if len(sys.argv) > 1 else (os.environ.get("GEMINI_API_KEY") or "AIzaSyBhJmRLcFtSOKTYAz21PsC-0xn_awo9rnY")
    models_to_try = [
        "gemini-2.5-flash-image",
        "gemini-3.1-flash-image",
        "gemini-3-pro-image",
    ]

    print(f"=== Nano Banana (Google Gemini Image) Sacred Mystery Generator ===")
    print(f"Total Mysteries: {len(MYSTERIES)}")
    print(f"Destination: {dest_dir.resolve()}\n")

    for idx, (filename, prompt) in enumerate(MYSTERIES, 1):
        out_path = dest_dir / f"{filename}.png"
        if out_path.exists() and out_path.stat().st_size > 10000:
            print(f"[{idx}/20] {filename}.png already exists. Skipping.")
            continue

        print(f"[{idx}/20] Generating {filename}.png with Nano Banana...")
        generated = False
        last_error = None

        for model in models_to_try:
            try:
                img_bytes = generate_with_nano_banana(api_key, model, prompt)
                with open(out_path, "wb") as f:
                    f.write(img_bytes)
                print(f"       -> Saved {out_path.name} ({len(img_bytes)} bytes) using {model}")
                generated = True
                break
            except urllib.error.HTTPError as e:
                err_body = e.read().decode("utf-8")
                last_error = f"HTTP {e.code}: {err_body}"
                if e.code == 429:
                    # Rate limit or quota 0 on free tier
                    break
            except Exception as e:
                last_error = str(e)

        if not generated:
            print(f"       [!] Generation failed: {last_error}")
            if "limit: 0" in str(last_error) or "429" in str(last_error):
                print("\n[NOTE] Google restricts the Nano Banana image API on Free Tier (limit: 0).")
                print("To enable API generation, link a Billing account / Pay-As-You-Go in Google AI Studio:")
                print(" -> https://aistudio.google.com/ (Select Project -> Set up Billing)")
                print("\nAlternatively, paste the prompts directly into Gemini or Google AI Studio for free:")
                print(" -> Prompts available in: NANO_BANANA_MYSTERIES_PROMPTS.md")
                break

    print("\nBatch process finished.")

if __name__ == "__main__":
    main()
