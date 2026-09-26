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
| `:core` | Functional helpers + future JNI bridge |

## Branch

Work happens on `modern-restructure`.

## Build

```bash
./gradlew :app:assembleDebug
```

(First time you may need to run `gradle wrapper` if the binary jar is missing.)

## Status

Skeleton only. Selective port of useful modernizable features from the original project comes next.
