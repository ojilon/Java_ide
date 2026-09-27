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

## Current status (see docs/ROADMAP.md for detail)

- Tasks 6–9 — editor shell, tabs, tokenizer highlighting ✅
- Tasks 10–13 — compile API + ECJ frontend + dex/packaging stubs + run flow ✅ (pipeline stops at DEX stub)
- Tasks 14+ — CMake/native, explorer, editor hardening, real dex/packaging, logcat, formatter, complete — Pending, in priority order

## Branch

All work is on **`modern-restructure`**.

## Build

```bash
./gradlew :app:assembleDebug
```
