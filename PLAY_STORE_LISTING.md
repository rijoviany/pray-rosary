# Google Play Console Listing Guide — Rosarium

A complete, production-ready Play Store listing kit for **Rosarium: The Holy Rosary**. Copy and paste directly into the Google Play Console.

---

## 1. App Basics

| Field | Value | Character Limit |
|---|---|---|
| **App Name** | `Rosarium: The Holy Rosary` | 30 chars max (26 used) |
| **Short Description** | `Pray the Holy Rosary with illuminated medieval art and tactile hold-to-pray.` | 80 chars max (79 used) |

---

## 2. Full Description (Formatted for Play Store)

```markdown
Immerse yourself in deep contemplative prayer with Rosarium, an authentic Catholic Holy Rosary app inspired by illuminated medieval manuscripts and sacred Renaissance art.

Designed for distraction-free devotion, Rosarium pairs physical prayer gestures with timeless sacred beauty.

✦ UNIQUE HOLD-TO-PRAY INTERACTION
• Tactile Prayer Physics: Pull down the active lapis lazuli bead and hold to recite. Text illuminates phrase-by-phrase in a golden karaoke cadence (~16 seconds).
• Physical Spring Snap: Upon prayer completion, the bead snaps upward into the decade loop with realistic spring physics, tactile clacks, and golden sparkles.
• Rotating Decade Loop: Just like fingering a physical rosary, the decade chain rotates bead-by-bead after each prayer, keeping the center receiving cradle ready for the next bead.
• Accessibility Mode: Prefer not to drag? Enable "Tap to Pray" in Settings for simple tap-and-hold devotion.

✦ THE FOUR HOLY MYSTERIES
• The Joyful Mysteries (Mondays & Saturdays)
• The Sorrowful Mysteries (Tuesdays & Fridays)
• The Glorious Mysteries (Wednesdays & Sundays)
• The Luminous Mysteries (Thursdays)
• Automatic Day Selection with manual override to pray any mystery at any time.
• Full Scripture references, biblical verses, and fruit of each mystery for rich meditation.

✦ BILINGUAL LOCALIZATION (ENGLISH & MALAYALAM)
• English: Traditional liturgical phrasing.
• Malayalam (മലയാളം): Complete vernacular translations (സ്വർഗ്ഗസ്ഥനായ ഞങ്ങളുടെ പിതാവേ, നന്മനിറഞ്ഞ മറിയമേ, ത്രിത്വസ്തുതി, ഫാത്തിമാ പ്രാർത്ഥന, പരിശുദ്ധ രാജ്ഞീ) with native typography and conjunct shaping.

✦ ILLUMINATED SACRED ART DIRECTION
• Aged parchment textures, ornate Gothic borders, gold-leaf accents, and fine ink typography.
• Specular 3D beads: Mother-of-pearl Our Father beads with gold ring caps, glossy lapis lazuli Hail Mary beads, and a Gothic trefoil Latin crucifix centerpiece.
• Sacred mystery fresco illustrations with atmospheric vignette for maximum legibility.

✦ SOUNDS & HAPTICS
• Realistic wooden/stone bead clack sounds on spring impact.
• Subtle string tension sound while holding the bead.
• Gentle medieval sanctuary chime upon prayer completion.
• Optional ambient Gregorian sacred chant drone.
• Rhythmic tactile haptic pulses guiding your meditation.

✦ OFFLINE-FIRST & 100% PRIVATE
• Zero Internet Required: Rosarium functions completely offline. Pray anywhere—in church, on flights, or during quiet retreats.
• Absolute Privacy: No accounts, no data collection, no telemetry, no tracking, and no advertisements.
• Auto-Resume: Automatically remembers your exact decade and bead count where you left off.
• Screen Keep-Awake: Screen stays gently illuminated while you pray.

"The Rosary is the weapon for these times." — St. Padre Pio

Download Rosarium and enrich your daily Catholic devotion today.
```

---

## 3. Categorization & Store Tags

- **Application Type**: App
- **Category**: Books & Reference / Lifestyle
- **Tags**:
  1. `Religion`
  2. `Christianity`
  3. `Catholic`
  4. `Prayer`
  5. `Meditation`

---

## 4. Content Rating Questionnaire Answers

When completing the Google Play **Content Rating** questionnaire (IARC):

| Question | Answer |
|---|---|
| **Category** | Reference / Utility / Educational |
| **Violence** | No |
| **Sexuality / Nudity** | No |
| **Language (Profanity)** | No |
| **Controlled Substances** | No |
| **User-generated content or sharing** | No |
| **Shares user location** | No |
| **Allows users to purchase digital goods** | No |
| **Resulting Rating** | **Everyone / PEGI 3 / USK 0** (Safe for all audiences) |

---

## 5. Target Audience & Families

- **Target Age Groups**: `18 and over`, `13–17`, `9–12` (Select `13 and over` if you want to avoid Google Play Families Policy review overhead, or select all for general audience).
- **Appeal to Children**: No (neutral UI).

---

## 6. App Permissions & Data Safety Form

Rosarium is 100% offline and requires zero sensitive user permissions.

### Manifest Permissions Declared:
- `android.permission.VIBRATE` (Normal/Safe permission for tactile bead clicks).

### Data Safety Form Answers:
1. **Does your app collect or share any user data?**
   - Select **"No"**.
2. **Data Collection**: No personal information, no location, no financials, no identifiers.
3. **Data Security**:
   - Since no data is collected, no transmission occurs over the network.
4. **Account Creation**: No accounts allowed or required.

---

## 7. Privacy Policy URL

Google Play requires a Privacy Policy URL for all apps. Since Rosarium collects zero data, you can host a simple static policy on GitHub Pages or Notion.

### Sample Privacy Policy Text:

> **Privacy Policy for Rosarium**  
> *Last updated: October 2026*  
>  
> Rosarium ("the App") is developed as an offline-first devotional tool for praying the Holy Rosary.  
>  
> **1. Data Collection and Usage**  
> Rosarium does not collect, transmit, store, or share any personal information, location data, device identifiers, or usage analytics. All prayer progress and settings are stored locally on your device using Android Jetpack DataStore.  
>  
> **2. Device Permissions**  
> The App only utilizes the device's vibration hardware (`android.permission.VIBRATE`) to provide tactile haptic feedback during prayer gestures.  
>  
> **3. Third-Party Services**  
> The App contains no third-party SDKs, advertising networks, or analytics trackers.  
>  
> **4. Contact**  
> If you have questions regarding this policy, contact us at: `your-email@domain.com`.

---

## 8. Store Graphic Assets Checklist

| Asset | Dimensions | Format | Requirements |
|---|---|---|---|
| **App Icon** | 512 × 512 px | 32-bit PNG (no alpha for Play Store) | High-res icon with illuminated golden cross and lapis bead. Max 1 MB. |
| **Feature Graphic** | 1024 × 500 px | JPG or 24-bit PNG | Aged parchment banner, gold title "Rosarium", lapis beads & crucifix. No text at edges. |
| **Phone Screenshots** | Min 2, Max 8 (e.g. 1080 × 2400 px) | JPG or 24-bit PNG | Aspect ratio 16:9 or 9:16. Upload the 3 panels: 1) Resting decade, 2) Hold-to-pray karaoke banner, 3) Mystery selector/progress. |

---

## 9. Release Notes (What's New in this Version)

```
en-US
Initial release of Rosarium:
• Authentic hold-to-pray bead gesture with realistic spring snap physics.
• Rotating decade rosary with empty filigree placeholders.
• Four Holy Mysteries (Joyful, Luminous, Sorrowful, Glorious) with scripture & meditations.
• Bilingual support: English and Malayalam (മലയാളം).
• 100% offline, privacy-first, zero advertisements.
```
