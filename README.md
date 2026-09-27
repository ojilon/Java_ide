# Java_ide

Modern, clean re-implementation of the Java N-IDE concept for Android.

## Goals

- **No legacy inheritance** from the original `a-java-ide`.
- Stick with **XML + Java**.
- Clear separation:
  - **`:app`** — Android UI (OOP)
  - **`:core`** — pure / functional logic + future JNI
- Modern Gradle (AGP 8.13.2, Java 17, compileSdk 36).

## Quick links

- **[docs/ROADMAP.md](docs/ROADMAP.md)** ← numbered task list (reply with a number)
- **[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)** — module & design rules
- **[ICONS.md](ICONS.md)** — launcher icon instructions

## Current status

- Foundation (Gradle, modules, icons, first functional ports) ✅
- Phase 1 (1–5) deferred / handled at compile time
- **Task 6** – Basic code editor UI ✅
- **Task 7** – File open / save stubs ✅

## Branch

All work is on **`modern-restructure`**.

## Build

```bash
./gradlew :app:assembleDebug
```
