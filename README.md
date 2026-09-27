# Java_ide

Modern, clean re-implementation of the Java N-IDE concept for Android.

## Goals

- **No legacy inheritance** from the original `a-java-ide`.
- Stick with **XML + Java**.
- Clear separation:
  - **`:app`** — Android UI (OOP)
  - **`:core`** — pure / functional logic + future JNI
- Modern Gradle (AGP 8.13.2, Java 17, compileSdk 36) taken from `Wayer` / `Conductino-Android`.

## Quick links

- **[docs/ROADMAP.md](docs/ROADMAP.md)** ← **numbered task list** (reply with a number to execute)
- **[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)** — module & package design
- **[ICONS.md](ICONS.md)** — how to replace the temporary launcher icons

## Current status

- Modern Gradle skeleton ✅
- Icon placeholders ✅
- Package structure + first functional ports (log parsing) ✅
- Docs & full task map ✅

## Branch

All work is on **`modern-restructure`**.

## Build

```bash
./gradlew :app:assembleDebug
```

(You may need to generate the wrapper jar once if it is missing.)
