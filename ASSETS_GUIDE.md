# Rosarium Asset Directory & Setup Guide

Rosarium is designed to render an authentic illuminated medieval manuscript aesthetic out of the box using high-resolution procedural vector rendering. You can also drop in custom PNG sprites and generated Midjourney/Flux/DALL-E illustrations.

---

## 1. Visual Sprites (`app/src/main/res/drawable-nodpi/`)

Place transparent 32-bit PNG assets into `app/src/main/res/drawable-nodpi/`:

| Filename | Dimensions | Description | Prompt Guide |
|---|---|---|---|
| `bead_blue.png` | 1024×1024 | Hail Mary bead | Lapis lazuli with gold flecks, 3/4 view, specular gloss |
| `bead_white.png` | 1024×1024 | Our Father bead | Pearl-white mother-of-pearl, gold ring caps |
| `chain_link.png` | 256×512 | Golden chain link | Seamless tileable antique brass-gold oval link |
| `crucifix_piece.png` | 512×768 | Centerpiece charm | Gothic trefoil gold Latin crucifix with center ruby |
| `banner_scroll.png` | 1600×500 | Prayer banner | Aged parchment scroll with torn edges & gold leaf |
| `home_parchment_bg.png`| 1080×2400 | Manuscript background | Aged parchment with gold vine border |

> **Note on Naming:** In Android, drawable resource files must be **lowercase alphanumeric** with underscores (no spaces, hyphens, or uppercase letters).

---

## 2. Mystery Artwork Illustrations (`app/src/main/res/drawable-nodpi/`)

Export all generated mystery backgrounds as 1080×2400 PNG or WebP files and name them:

### Joyful Mysteries
- `mystery_joyful_1.png` — The Annunciation
- `mystery_joyful_2.png` — The Visitation
- `mystery_joyful_3.png` — The Nativity
- `mystery_joyful_4.png` — The Presentation
- `mystery_joyful_5.png` — Finding in the Temple

### Luminous Mysteries
- `mystery_luminous_1.png` — The Baptism in the Jordan
- `mystery_luminous_2.png` — The Wedding at Cana
- `mystery_luminous_3.png` — Proclamation of the Kingdom
- `mystery_luminous_4.png` — The Transfiguration
- `mystery_luminous_5.png` — Institution of the Eucharist

### Sorrowful Mysteries
- `mystery_sorrowful_1.png` — The Agony in the Garden
- `mystery_sorrowful_2.png` — The Scourging at the Pillar
- `mystery_sorrowful_3.png` — The Crowning with Thorns
- `mystery_sorrowful_4.png` — The Carrying of the Cross
- `mystery_sorrowful_5.png` — The Crucifixion

### Glorious Mysteries
- `mystery_glorious_1.png` — The Resurrection
- `mystery_glorious_2.png` — The Ascension
- `mystery_glorious_3.png` — Descent of the Holy Spirit
- `mystery_glorious_4.png` — The Assumption of Mary
- `mystery_glorious_5.png` — Coronation of Mary

---

## 3. Audio Sound Effects (`app/src/main/res/raw/`)

Supported formats: `.ogg`, `.wav`, or `.mp3`. Place them directly in `app/src/main/res/raw/`:

1. `bead_clack.ogg` / `bead_clack.wav`
   - Triggered on spring snap-up bounce impacts.
   - Recommended: Short, wooden bead or polished stone clack (100–250ms).
2. `string_tension.ogg` / `string_tension.wav`
   - Triggered when bead is pulled past the drag threshold.
   - Recommended: Subtle creak or soft tension pluck.
3. `prayer_bell.ogg` / `prayer_bell.wav`
   - Triggered when hold-to-pray karaoke text finishes.
   - Recommended: Warm medieval sanctuary chime or bell harmonic.
4. `ambient_chant.ogg` / `ambient_chant.mp3` (Optional)
   - Background audio when enabled in Settings.
   - Recommended: Seamlessly looping low Gregorian drone or monastic chant.
