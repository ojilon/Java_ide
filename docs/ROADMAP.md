# Java_ide — Roadmap & Task Map

All work happens on branch `modern-restructure`.

Reply with the **number** of the task you want done next.  
Tasks are ordered roughly by dependency / value, but you can jump around.

---

## Phase 0 — Foundation (mostly done)

| # | Task | Status |
|---|------|--------|
| 0.1 | Modern Gradle skeleton (AGP 8.13.2, Java 17, SDK 36) | ✅ Done |
| 0.2 | Module split: `:app` (OOP UI) + `:core` (functional / JNI-ready) | ✅ Done |
| 0.3 | Icon placeholders + `ICONS.md` instructions | ✅ Done |
| 0.4 | First functional ports: `LogLine`, `SearchCriteria`, `StringOps` | ✅ Done |
| 0.5 | Create `docs/` and this roadmap | ✅ Done (this commit) |

---

## Phase 1 — Make it runnable & usable

| # | Task | Notes |
|---|------|-------|
| **1** | Add real launcher PNGs (or keep vectors) + verify build | Use Android Studio Image Asset or supply PNGs. Update `ICONS.md` if needed. |
| **2** | Add Gradle Wrapper binary (`gradle-wrapper.jar`) + `gradlew` / `gradlew.bat` | So `./gradlew assembleDebug` works out of the box. |
| **3** | Basic Logcat UI screen | RecyclerView that displays `LogLine`s, filter bar that uses `SearchCriteria`. Wire into MainActivity or a new fragment. |
| **4** | Sample / mock log data generator | Functional helper in `:core` that produces realistic log lines for UI testing without needing a real device logcat. |
| **5** | Simple project model (in-memory) | Immutable `Project` / `SourceFile` records in `:core`. No disk yet. |

---

## Phase 2 — Editor surface

| # | Task | Notes |
|---|------|-------|
| **6** | Basic code editor UI (EditText or better) | Syntax-highlight-ready surface in `ui/editor`. Start simple. |
| **7** | File open / save stubs | UI + core APIs. Real filesystem later. |
| **8** | Tabbed editor (multiple open files) | ViewPager2 or custom tab layout. |
| **9** | Basic syntax highlighting (Java keywords) | Pure Java tokenizer in `:core` (functional), applied in UI. Easy JNI candidate later. |

---

## Phase 3 — Build & Run pipeline (modern, no legacy)

| # | Task | Notes |
|---|------|-------|
| **10** | Design compile API in `:core` | Interface only at first: `CompileRequest` → `CompileResult`. |
| **11** | Integrate a modern Java compiler frontend | Prefer something maintainable (e.g. ecj or a lightweight path). No old JDK 1.7 modules. |
| **12** | Dexing / packaging stubs | Placeholder that can later call `d8` / `r8`. |
| **13** | “Run on device” flow (install + launch) | Use modern PackageInstaller / adb ideas. |

---

## Phase 4 — JNI / C++ readiness

| # | Task | Notes |
|---|------|-------|
| **14** | Enable CMake in `:core` | Uncomment / flesh out the externalNativeBuild block. |
| **15** | First native method (`nativeHello`) | Prove the JNI seam works end-to-end. |
| **16** | Port `LogLine.parse` (or tokenizer) to C++ | Demonstrate the “easy to port” goal. |
| **17** | Memory / performance notes for hot paths | Document which pieces should stay in native. |

---

## Phase 5 — Polish & extra features

| # | Task | Notes |
|---|------|-------|
| **18** | Settings screen (theme, font size, etc.) | OOP PreferenceFragment or modern equivalent. |
| **19** | Dark / light theme polish | Material 3 dynamic color where useful. |
| **20** | Project explorer (real tree) | Connect to the project model from task 5. |
| **21** | Logcat live capture (real device) | Optional; use modern logcat APIs if possible. |
| **22** | Code formatter integration | Google Java Format or similar, kept behind a clean interface. |
| **23** | Basic auto-complete (keywords + simple symbols) | Start in pure Java; later can move to native. |
| **24** | README / docs polish + screenshots | Keep docs in sync with reality. |
| **25** | CI (GitHub Actions) for assembleDebug | Simple build check on every push. |

---

## How to use this file

1. Reply with a number, e.g. `3` or `do 3`.
2. I will implement that task on `modern-restructure`, commit, and push.
3. After each task I will list the next recommended numbers.

You can also say “do 3 and 4” or “skip to phase 2”.
