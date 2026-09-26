# Java_ide

Modern, clean re-implementation of the Java N-IDE concept for Android.

## Goals

- **No legacy inheritance** from the original `a-java-ide` (old AGP 3.x, AOSP forks, JDK 1.7 modules, etc.).
- Stick with **XML + Java**.
- Clear separation of concerns:
  - **`:app`** — Android UI layer (Activities, Fragments, ViewModels, adapters) written in classic **OOP** style.
  - **`:core`** — background / pure logic written in a more **functional** style, designed for easy later port of selected parts to a **C++ backend via JNI**.
- Modern Gradle (AGP 8.13.2, Gradle 8.14.x, Java 17, compileSdk 36) borrowed from the setup used in `Wayer` and `Conductino-Android`.

## Modules

| Module | Role |
|--------|------|
| `:app` | Launcher, UI, Android framework integration (OOP) |
| `:core` | Functional helpers + log parsing + future JNI bridge |

## Package layout

```
app/
  ui/                 ← Activities / Fragments (OOP)
    editor/           ← source editor UI
    project/          ← project explorer UI
core/
  functional/         ← pure helpers (StringOps, …)
  log/                ← immutable LogLine + SearchCriteria (modernized)
  jni/                ← NativeBridge (future C++ seams)
```

## Icons

Temporary vector adaptive icons are in place so the project builds.  
See **[ICONS.md](ICONS.md)** for the exact PNG names, densities, and how to replace them.

## Branch

Work happens on `modern-restructure`.

## Build

```bash
./gradlew :app:assembleDebug
```

(First time you may need to run `gradle wrapper` if the binary jar is missing.)

## Status

- Modern Gradle skeleton ✅
- Icon placeholders + instructions ✅
- Package structure + first functional ports (log parsing) ✅
- Next: more selective modern ports / real editor surface
