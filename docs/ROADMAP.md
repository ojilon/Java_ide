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

Tasks **1–5** are deferred.

---

## Phase 2 — Editor surface (done)

| # | Task | Status |
|---|------|--------|
| **6** | Basic code editor UI | ✅ Done |
| **7** | File open / save stubs | ✅ Done |
| **8** | Tabbed editor | ✅ Done |
| **9** | Basic syntax highlighting | ✅ Done |

---

## Phase 3 — Build & Run pipeline

| # | Task | Detailed plan | Status |
|---|------|---------------|--------|
| **10** | Design compile API | Pure interfaces: CompileRequest → CompileResult + Compiler | ✅ Done |
| **11** | Modern Java compiler frontend | Real backend implementing `Compiler` (e.g. ECJ). | Pending |
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

| # | Task | Status |
|---|------|--------|
| **18** | Settings screen | Pending |
| **19** | Dark / light theme polish | Pending |
| **20** | Project explorer | Pending |
| **21** | Live logcat capture | Pending |
| **22** | Code formatter | Pending |
| **23** | Basic auto-complete | Pending |
| **24** | Docs & screenshots | Pending |
| **25** | CI | Pending |

---

## How to use

Reply with a number (e.g. `11`). I implement it on `modern-restructure` and push.
