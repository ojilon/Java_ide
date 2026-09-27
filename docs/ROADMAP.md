# Java_ide — Roadmap & Task Map

All work happens on branch `modern-restructure`.

Reply with the **number** of the task you want done next.

---

## Phase 0 — Foundation (done)

| # | Task | Status |
|---|------|--------|
| 0.1–0.5 | Gradle, modules, icons, first ports, docs | ✅ Done |

---

## Phase 1 — Deferred (mocks / compile-time)

Tasks **1–5** are deferred — handled when compiling / running in Android Studio.

---

## Phase 2 — Editor surface

| # | Task | Status |
|---|------|--------|
| **6** | Basic code editor UI | ✅ Done |
| **7** | File open / save stubs | ✅ Done |
| **8** | Tabbed editor (ViewPager2 + TabLayout) | ✅ Done |
| **9** | Basic syntax highlighting (pure tokenizer in `:core`) | ✅ Done |

---

## Phase 3 — Build & Run pipeline

| # | Task | Detailed plan | Status |
|---|------|---------------|--------|
| **10** | Design compile API | Pure interfaces in `:core`: `CompileRequest` → `CompileResult` | Pending |
| **11** | Modern Java compiler frontend | Embeddable compiler (e.g. ECJ). Expose only via task-10 API. | Pending |
| **12** | Dexing / packaging stubs | Thin wrappers for later `d8` / `r8`. | Pending |
| **13** | “Run on device” flow | PackageInstaller / launch. | Pending |

---

## Phase 4 — JNI / C++ readiness

| # | Task | Detailed plan | Status |
|---|------|---------------|--------|
| **14** | Enable CMake in `:core` | Minimal `CMakeLists.txt` + stub `.cpp`. | Pending |
| **15** | First native method | `nativeHello()` end-to-end. | Pending |
| **16** | Port hot path to C++ | Tokenizer or `LogLine.parse`. | Pending |
| **17** | Performance notes | Document Java vs native candidates. | Pending |

---

## Phase 5 — Polish & extra features

| # | Task | Detailed plan | Status |
|---|------|---------------|--------|
| **18** | Settings screen | Theme, font size, tab size. | Pending |
| **19** | Dark / light theme polish | Material 3. | Pending |
| **20** | Project explorer | Tree of open / project files. | Pending |
| **21** | Live logcat capture | Optional. | Pending |
| **22** | Code formatter | Behind clean `:core` interface. | Pending |
| **23** | Basic auto-complete | Keywords + simple symbols. | Pending |
| **24** | Docs & screenshots | Keep in sync. | Pending |
| **25** | CI | `assembleDebug` on push. | Pending |

---

## How to use

Reply with a number (e.g. `10`). I implement it on `modern-restructure` and push.
