# App Icons — How to replace the placeholders

The project currently ships **vector adaptive icons** (API 26+) so the app builds and installs without real PNGs.  
These are temporary. Replace them with proper density-specific PNGs for production quality.

## Required PNG files and exact names

Place the files under `app/src/main/res/` using these **exact names**:

| Density folder              | Files to add                                      | Recommended size |
|-----------------------------|---------------------------------------------------|------------------|
| `mipmap-mdpi/`              | `ic_launcher.png`<br>`ic_launcher_round.png`      | 48 × 48 px       |
| `mipmap-hdpi/`              | `ic_launcher.png`<br>`ic_launcher_round.png`      | 72 × 72 px       |
| `mipmap-xhdpi/`             | `ic_launcher.png`<br>`ic_launcher_round.png`      | 96 × 96 px       |
| `mipmap-xxhdpi/`            | `ic_launcher.png`<br>`ic_launcher_round.png`      | 144 × 144 px     |
| `mipmap-xxxhdpi/`           | `ic_launcher.png`<br>`ic_launcher_round.png`      | 192 × 192 px     |

Optional (for pre-API-26 fallback if you remove the anydpi adaptive icons):

- Same names in the folders above are enough; Android will pick the closest density.

## Adaptive icon layers (recommended)

If you prefer to keep adaptive icons (modern Android):

1. Keep or replace:
   - `drawable/ic_launcher_background.xml` (or a solid-color / PNG background)
   - `drawable/ic_launcher_foreground.xml` (or a 108×108 safe-zone PNG)
2. The files under `mipmap-anydpi-v26/` already point to those drawables.

Foreground safe zone: keep important content inside the central 66 dp circle / 72 dp square.

## Quick generation options

- Android Studio → right-click `res` → **New → Image Asset** (easiest).
- Or use any icon generator and export the density set with the names above.
- Then delete the `.gitkeep` files in the mipmap-* folders.

After adding real PNGs you can optionally delete the vector foreground/background if you no longer need them.
