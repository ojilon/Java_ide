# Architecture

> Companion to `docs/ROADMAP.md`. Rules first, then current state, then target.
> Reference only: `a-java-ide-master/` (ignored) is never copied — ideas are reimplemented.

## 1. Design rules (enforced in review)

- **`:core` = pure-functional Java.** No `android.content.*`, no `Context`, no `File` in *new* public signatures (legacy `EcjCompiler` temp-file use is internal and stays internal). Only `androidx.annotation` allowed. Immutable value objects + Builder, `unmodifiableList`, defensive `byte[].clone()`.
- **`:app` = OOP Android.** ViewBinding (already `viewBinding=true` — migrate off `findViewById`), ViewModel + SavedState, background executor/coroutines, callbacks on main thread.
- **Single seam:** `CoreBridge` (`core/CoreBridge.java`) is the only entry from `:app` into `:core`. Backends swappable via `setCompiler/setDexer/setPackager` (fakes in tests).
- **JNI-ready:** `jni/NativeBridge.java` owns every `native` decl + `loadLibrary`. Hot pure functions (`syntax/*`, `functional/StringOps`, `log/*` parse/match) stay static + stateless + `String/byte[]/int` so they port 1:1 to `core/src/main/cpp/`.

## 2. Current pipeline (tasks 10–13, as built)

```
Editor "Run" (EditorFragment)
      │  RunRequest(SourceFile + PackageOptions)
      ▼
BuildAndRunCoordinator (app/run, SingleThreadExecutor + Handler(main))
      │
      ├─ CoreBridge.compile()    → EcjCompiler (real, ECJ 3.42.0, temp src/out dirs)
      ├─ CoreBridge.dex()        → StubDexer (always notImplemented — pipeline stops here)
      ├─ CoreBridge.packageApk() → StubPackager (always notImplemented)
      ├─ ApkInstaller.install()  → PackageInstaller session (real, modern API)
      └─ ApkInstaller.launch()   → launch intent (real)
```

- Stage progress via `core/run/RunStage` (`SAVE,COMPILE,DEX,PACKAGE,INSTALL,LAUNCH,DONE,FAILED`) + `Callback.onStage/onFinished` posted to main.
- `RunResult(ok/fail)` accumulates `List<Diagnostic>` across stages.
- When real dex/package backends land (tasks 24–27), the same coordinator works end-to-end — no coordinator rewrite, just backend swap.

## 3. Packages (current → target)

```
core/                          # pure, JNI-ready
  compile/   Compiler, CompileRequest/Options/Result, CompiledClass,
             Diagnostic(+Severity), EcjCompiler          # 22-23: multi-file + android.jar + col
  dex/       Dexer, DexRequest/Options/Result, StubDexer # 24-25: + D8Dexer (r8 artifact)
  packaging/ Packager, PackageRequest/Options/Result, StubPackager
                                                       # 26-27: + AaptService, ApkBuilder, ApkPackager(apksig)
  aapt/      (new, task 26) AaptService, StubAapt
  apk/       (new, task 27) ApkBuilder
  format/    (new, task 30) Formatter, GoogleJavaFormatAdapter
  template/  (new, task 33) ProjectTemplate
  decompile/ (new, task 38) Decompiler (stub only)
  run/       RunRequest, RunResult, RunStage
  syntax/    JavaTokenizer, Token, TokenType            # 16: C++ port, JNI int[] triples
  log/       LogLine, LogLevel, SearchCriteria          # 16: C++ port; 29: Flow<LogLine> in :app
  model/     SourceFile, DocumentStore                 # 18: + ProjectFile, FileRepository
  file/      (new, task 18) ProjectFile, FileRepository
  functional/StringOps                                 # 16: C++ port
  jni/       NativeBridge                              # 14-15: loadLibrary + nativeHello

app/                           # Android UI (OOP)
  run/       BuildAndRunCoordinator, ApkInstaller, InstallResultReceiver
                                                       # 28: per-run UUID listeners, timeout, bottom-sheet
  ui/        MainActivity (hosts EditorFragment)
    editor/  EditorFragment (tabs host), EditorPageFragment (one tab),
             EditorPagerAdapter (ViewPager2), SyntaxHighlighter (core tokens → spans)
                                                       # 20-21: ViewBinding, ViewModel, debounce + theme spans
             ProblemsFragment (new, task 32)
    project/ ProjectExplorerFragment (stub → task 19 RecyclerView + DiffUtil)
    logcat/  (new, task 29) LogcatFragment + ViewModel + Repository
    settings/(new, task 34) SettingsFragment (DataStore)
  logcat/    (new, task 29) LogcatRepository (logcat process → Flow)
```

## 4. Data flow (target, tasks 18–29)

```
SAF files ──► FileRepository ──► EditorViewModel ──► EditorFragment/Pager
                                          │                │ tokenize (bg, debounced)
                                          │                ▼
                                          │         SyntaxHighlighter (theme spans + error underlines)
                                          ▼
                              CompileRequest(ProjectSources)
                                          │
                     EcjCompiler ──► D8Dexer ──► AaptService ──► ApkPackager(apksig)
                                          │         (diagnostics → ProblemsFragment)
                                          ▼
                              BuildAndRunCoordinator ──► ApkInstaller ──► launch
                                          │
                              LogcatRepository ──► LogcatFragment (SearchCriteria filter)
```

## 5. What we deliberately do NOT port

- `aosp/` (1265 files): depend on `aapt2`/Maven artifacts instead (task 26).
- `jdk-1_7/` (521 files): ECJ covers it (task 11 done).
- `dx/` (386 files): use D8/R8 artifact (tasks 24–25).
- `treeview/com.unnamed.b.atv`, Catlog fork, regex autocomplete resolvers, Fernflower fork: reimplement the *idea* with RecyclerView/Flow/ECJ-AST (tasks 19/29/31/38).
- Closed `lib-n-ide-release-10.aar` editor: replaced by open tokenizer + (later) Sora-grade view.
