# Java_ide — Roadmap & Task Map

All work happens on branch `modern-restructure`.

Reply with the **number** of the task you want done next (e.g. `14`).

> **Ground rules for every task (non-negotiable):**
> 1. **Ideas only, never code copy** from `a-java-ide-master/` (ignored folder). That tree is reference for *what* the old N-IDE did, not *how*. Reimplement clean-room.
> 2. **Modern split:** `:core` = pure-functional Java, no Android imports except `androidx.annotation`. Immutable value objects + Builder + `unmodifiableList` + defensive `clone()` for `byte[]`. `:app` = OOP Android layer, ViewBinding, ViewModel/Flow, background executors, main-thread callbacks.
> 3. **JNI-ready:** every hot pure function (`tokenize`, `parseLogLine`, `matches`, `trimOrEmpty`, diagnostic parsing, hashing) must be static, stateless, `String/byte[]/int`-only signatures so it can be pushed to `core/src/main/cpp/` + `NativeBridge` later. No `File`, no `Context` in `:core` public API (new code).
> 4. **No regressions:** keep `CoreBridge` as the only seam (`setCompiler/setDexer/setPackager` for fakes in tests).

---

## 0. Where we are (audit 2026-09-27)

### Done — Phases 0–3

| # | Task | What exists (modern) | Legacy inspiration |
|---|------|----------------------|--------------------|
| 6 | Basic editor UI | `app/.../ui/MainActivity.java` (26 lines, hosts `EditorFragment`), `fragment_editor.xml` | `a-java-ide-master/app/.../JavaIdeActivity.java` — idea of IDE shell only |
| 7 | File open/save stubs | `core/.../model/SourceFile.java` (immutable `id/name/content`), `DocumentStore.java` (synchronized in-memory `LinkedHashMap`), `CoreBridge.open/save/list` | `app/.../utils/FileUtils.java`, `FileChangeListener.java` — idea only; we are still memory-only |
| 8–9 | Tabbed editor + highlighting | `EditorFragment.java` + `EditorPagerAdapter.java` (ViewPager2 + TabLayoutMediator) + `EditorPageFragment.java` + `SyntaxHighlighter.java` ↔ `core/.../syntax/JavaTokenizer.java`, `Token.java`, `TokenType.java` | Closed `lib-n-ide-release-10.aar` editor + `spans/BracketSpan,CustomUnderlineSpan` — replaced with open hand-rolled stateless tokenizer |
| 10 | Compile API | `core/.../compile/Compiler.java`, `CompileRequest/Options/Result/CompiledClass/Diagnostic/Severity.java` — immutable + factories | `lib-android-compiler/.../builder/task/java/CompileJavaTask.java`, `Java.execCompile()` — idea of task-shaped compile step |
| 11 | ECJ frontend | `core/.../compile/EcjCompiler.java` (~256 lines, `BatchCompiler`, temp `src/out` dirs, regex diag parse, `collectClasses`) | `jdk-1_7/.../com/sun/tools/javac/main/*` in-process javac — idea only; we use ECJ 3.42.0 standalone instead |
| 12 | Dex/packaging stubs | `core/.../dex/Dexer,DexRequest,DexOptions,DexResult,StubDexer` + `packaging/Packager,PackageRequest,PackageOptions,PackageResult,StubPackager` | `dx/.../com/duy/dx/*` (386 files), `lib-android-compiler/.../builder/{AndroidAppBuilder,JavaBuilder,JarBuilder}` + `task/android/*,task/java/DexTask,JarTask` |
| 13 | Run-on-device flow | `app/.../run/BuildAndRunCoordinator.java` (single-thread executor, `COMPILE→DEX→PACKAGE→INSTALL→LAUNCH`), `ApkInstaller.java` (PackageInstaller session), `InstallResultReceiver.java` (broadcast) + `core/.../run/RunRequest,RunResult,RunStage.java` | `app/.../run/activities/ExecuteActivity.java`, `run/view/ConsoleEditText.java` (console idea) |
| — | Log/format pure fns | `core/.../log/LogLine,LogLevel,SearchCriteria.java`, `functional/StringOps.java` | `androidlogcat/.../data/LogLine.java`, `reader/LogcatReader.java`, `common/.../IOUtils.java`, `lib-google-java-format/.../Formatter.java` |

### Known gaps / tech debt in current code (fix inside tasks below)

- `core/build.gradle` declares `externalNativeBuild.cmake` + `cppFlags -std=c++23` but **no `CMakeLists.txt` / `src/main/cpp/` exists** → Task 14.
- `NativeBridge.java` is 100% commented (`nativeHello`, `parseLogLineNative`) → Task 15.
- `EcjCompiler.DIAG_HEADER` regex is fragile (drops column, filters `^` echo heuristically), `quote()` only handles spaces, `deleteRecursively` ignores result, catches `Exception` → noisy `INFO` stacktrace, no cancellation/timeout, no `android.jar` support → Tasks 22–23.
- `StubDexer` / `StubPackager` always `notImplemented()` → pipeline **always stops at DEX** → Tasks 24–27.
- `PackageOptions.minSdk/targetSdk` are `String` (should be `int`), no manifest/resources/signing model, in-memory `byte[]` will OOM on real APKs → Task 27.
- `DocumentStore` is memory-only (no SAF, no dirty flag, rotation loses tabs) → Tasks 18–19.
- `EditorPageFragment.afterTextChanged → setText(highlight)` on **every keystroke on UI thread**, cursor-restore hack, `IDENTIFIER` span = noise + perf, fixed colors not theme-aware, `findViewById` despite `viewBinding=true`, `saveCurrent/runCurrent` uses `getChildFragmentManager().getFragments()+isVisible()` (fragile) → Tasks 20–21.
- `BuildAndRunCoordinator.executor` never `shutdown()`, static `InstallResultReceiver.listener` overwritten per-run + never cleared (race), no install timeout, no `abandon()` on commit throw, hardcoded `"generated.apk"`, `RunStage.SAVE/DONE/FAILED` never emitted → Tasks 28–29.
- `ProjectExplorerFragment.java` is a 23-line stub returning `new View()`, never wired → Task 19.
- `NotImplementedCompiler`, `CoreBridge.hello()` are dead code → remove in Task 22.
- No `core` JVM unit tests, no CI → Task 36.
- `README.md` still lists Tasks 6–7 as current; `ARCHITECTURE.md` only covers tasks 10–13 → fixed by this docs rewrite.

### Legacy → modern coverage map (ideas drawn from `a-java-ide-master/`)

| Legacy capability | Legacy location | Modern status | Modern target task |
|---|---|---|---|
| Java editor + tabs | `app/.../JavaIdeActivity.java`, closed `.aar` | Basic EditText tabs done | 20–21 (Sora-grade editor) |
| `javac` on-device | `jdk-1_7/` (521 files), `Java.execCompile()` | ECJ done, needs hardening | 22–23 |
| `.class → .dex` | `dx/` (386 files), `DexTask.java` | Stub | 24–25 (D8) |
| APK pipeline (aapt/merge/package/sign) | `aosp/` (1265 files), `AndroidAppBuilder.java`, `ProcessAndroidResourceTask`, `PackageApkTask`, `SignApkTask.java`, `bouncycastle/` | Stub | 26–27 (aapt2/apksig) |
| Project explorer tree | `treeview/` (5 files `AndroidTreeView/TreeNode`), `FolderStructureFragment.java`, `ProjectFilePresenter.java`, `DialogNew*` | Stub | 19 |
| Logcat viewer | `androidlogcat/` (~60 files, Catlog fork) | Pure `LogLine/SearchCriteria` only, no UI | 29 |
| Formatter | `lib-google-java-format/` (`Formatter,RemoveUnusedImports,ImportOrderer`) | None | 30 |
| Autocomplete | `app/.../editor/autocomplete/*` (`JavaAutoCompleteProvider`, `ExpressionResolver`, `TypeResolver`, regex-based) | None | 31 |
| Diagnostics → editor markers | `diagnostic/parser/java/*`, `diagnostic/parser/aapt/*` | ECJ diags as Toast only | 23, 32 |
| Run console (stdin/stdout) | `ExecuteActivity.java`, `ConsoleEditText.java`, `IntegerQueue/ByteQueue` | Toast + toolbar subtitle only | 28 |
| Templates/wizards/samples | `lib-android-compiler/.../project/Template,JavaProject,AndroidAppProject.java`, `DialogNewJavaProject/DialogNewAndroidProject.java`, `sample/AssetUtil.java` | Hardcoded `Hello.java` sample only | 33 |
| Layout preview | `app/.../uidesigner/inflate/Inflater.java`, `DynamicLayoutInflator.java` | None (explicitly deferred) | 37 (spike only) |
| Decompiler | `lib-decompiler/` (Fernflower fork) | None | 38 (deferred, use-case seam only) |
| Debugger (jdb/DAP) | Missing in legacy (TODO) + unused `aosp/ddmlib` | None | 39 (deferred, research only) |

---

## Phase 4 — JNI / C++ readiness (do first — unblocks perf story)

| # | Task | Legacy idea | Modern design | Acceptance |
|---|------|-------------|---------------|------------|
| **14** | Enable CMake in `:core` | None (new) | Add `core/src/main/cpp/CMakeLists.txt` (`javaide_core`, `c++_shared`, C++23), `core/src/main/cpp/javaide_core.cpp` with `JNI_OnLoad` stub, wire `externalNativeBuild` in `core/build.gradle` (already half-declared), keep JVM fallback if `.so` missing. | `./gradlew :core:assembleDebug` + `:core:assembleRelease` produce `libjavaide_core.so` for configured ABIs; no change to Java API. |
| **15** | First native method | None (new) | Uncomment + shape `NativeBridge`: `static { loadLibrary }` guarded by `try/catch`, `nativeHello(): String`, `parseLogLineNative(String): long`-or-better `int[]`. Java side keeps pure fallback (`LogLine.parse`). Add `core/src/test` JVM test asserting fallback == native (when present). | App About/Diagnostics screen (or logcat) shows `nativeHello()` when `.so` present, fallback string otherwise; never crashes without `.so`. |
| **16** | Port hot path to C++ | `JavaTokenizer` (modern) replaces legacy spans idea | Port `JavaTokenizer.tokenize` + `StringOps.trimOrEmpty/isBlank` + `LogLine.parse` prefix-scan to C++ with identical offsets. JNI takes `String` → returns `int[]` (`type,start,end` triples) to avoid object churn. Java wrapper converts to `List<Token>`. Fuzz-compare Java vs C++ on sample files. | Benchmark note in code: C++ path used when `System.loadLibrary` succeeded; unit test `TokenizerParityTest` passes; highlighting unchanged visually. |
| **17** | Performance notes | `dx/.../dex/*` perf sensitivity (idea: measure before porting) | Add `docs/PERF.md`: how to measure (Android Studio profiler + `System.nanoTime` microbench in `core` test), what we measured (tokenize ms/KB, ECJ ms, dex placeholder), rule "port only hot pure functions". No code change beyond docs + one `@VisibleForTesting benchmark()` hook. | `docs/PERF.md` exists with numbers from a real device; roadmap tasks 16/25/30 reference it. |

---

## Phase 5 — Editor & project foundations (biggest user-visible win)

| # | Task | Legacy idea | Modern design | Acceptance |
|---|------|-------------|---------------|------------|
| **18** | File persistence (SAF + ViewModel) | `FileUtils.java`, `FileChangeListener.java` | New `core/file/ProjectFile.java` (immutable `path/content/dirty/hash`), `FileRepository` interface (`open/save/list/hash`) with `InMemory` (tests) + `SafFileRepository` (`:app`, DocumentFile/SAF) impls. `EditorViewModel` (SavedStateHandle) owns `fileIds` so rotation keeps tabs. `DocumentStore` becomes cache, not source of truth. All I/O off main thread (coroutines or executor). | Rotate device → tabs + unsaved edits survive; open/save via SAF works on Android 13+; `core` has no `Context`. |
| **19** | Project explorer | `treeview/AndroidTreeView.java`, `TreeNode.java`, `FolderStructureFragment.java`, `FolderHolder.java`, `ProjectFilePresenter/Contract.java`, `DialogNewClass/Folder/Copy/DeleteFile.java` | **Reimplement, don't port:** `ProjectExplorerFragment` (real RecyclerView + DiffUtil, ViewBinding) + `FileTreeRepository.listFlow(root): Flow<List<FileNode>>` + `FileNode` immutable (`name/isDir/childrenHash`). CRUD via dialogs → `Rename/Create/DeleteUseCase` in `:core` (pure path logic) + `:app` SAF execution. Long-press menu, icons via Material. Never import `com.unnamed.b.atv`. | Explorer shows real project tree, expand/collapse, new class/folder/file, rename, delete (with confirm); works with Task 18 repo. |
| **20** | Editor correctness + ViewBinding | `JavaIdeActivity` menu idea, `JavaMenuManager.java` | Migrate `EditorFragment`, `EditorPageFragment`, `MainActivity` to ViewBinding (already `viewBinding=true`). Replace fragile `getFragments()+isVisible()` with `adapter.getFileId(viewPager.currentItem)` + `ViewModel.getContent(fileId)`. Save = push EditText → ViewModel → `CoreBridge.saveDocument`; Run = read from ViewModel, not fragment. Diagnostics go to bottom-sheet/log view, not 10-line Toast. | No `findViewById` in editor package; `lint` clean; save/run work with offscreen pages; rotation safe. |
| **21** | Highlight perf (debounce + theme) | `spans/*`, `JavaIdeCodeFormatProvider.java` | Debounce 250–300 ms (Handler/coroutine), tokenize on background thread, apply `Spannable` diff (only changed spans), drop `IDENTIFIER` coloring, theme-aware palette (`?attr/colorPrimary` + night `values-night/colors.xml`), underline spans for errors (map `Diagnostic` → `CustomUnderlineSpan` idea reimplemented). Keep `JavaTokenizer` in `:core` untouched. Cancel in-flight job on new keystroke. | 5k-line file types without jank (manual test + `PERF.md` number); dark/light correct; error underlines appear from Task 23. |

---

## Phase 6 — Build pipeline: real compile → dex → package

| # | Task | Legacy idea | Modern design | Acceptance |
|---|------|-------------|---------------|------------|
| **22** | Compile API v2 (multi-file project) | `BuilderImpl/IBuilder.java`, `JavaBuilder.java`, `CompileJavaTask.java` | Extend `CompileRequest` to `ProjectSources(List<SourceFile>, mainClass?)` + `CompileOptions(androidJar?: String, release?: int)`. `CoreBridge.compile(project)` overload. Delete `NotImplementedCompiler` + `hello()` dead code. `RunRequest` carries `List<SourceFile>` (keep single-file overload for compat). Pure, no `File` in signature (impl may use temps internally). | Existing single-file callers still compile; new test compiles 3-file package (`demo.Hello` + helper) via ECJ. |
| **23** | ECJ hardening + diagnostics → markers | `diagnostic/parser/java/JavaOutputParser,JavaErrorParser.java`, `AaptOutputParser/Error1-8Parser.java` (idea: stderr → line/col markers) | Fix `DIAG_HEADER` to capture column when present, robust `quote()` (quotes + escapes), check `mkdirs/delete` results, cancellation token (`AtomicBoolean`/coroutine cancel → `CompilationProgress.isCanceled`), timeout, `android.jar` on classpath option, map `fileName` back to `SourceFile.id` (not just name). `Diagnostic` gets `column` plumbed end-to-end (already has field). Publish `DiagnosticSet → List<SpanMark>` pure mapper in `:core`. | Wrong-code sample shows red underlines at correct line/col; `EcjCompilerTest` (JVM) covers error/warning/column/timeout; no `printStackTrace` noise (use `Log` or diag). |
| **24** | Dex backend interface v2 | `DexTask.java`, `dx/command/dexer/Main.java` | Evolve `DexRequest(classes: List<CompiledClass>, minApi: Int)` to also accept `classFiles: List<File>`-or-`Map<binaryName,bytes>` + streaming to avoid OOM. `DexOptions(minApi: Int, debuggable: Boolean)`. Keep `Dexer` interface stable; add `D8Dexer implements Dexer` (dependency `com.android.tools:r8`, JVM-first so it unit-tests). `StubDexer` stays as fallback. | `:core` JVM test: ECJ Hello → D8 → non-empty dex bytes; `notImplemented()` only when D8 absent. |
| **25** | D8 on-device wiring | `dx/dex/cf/*`, `dex/file/*`, `Dex.java`, `multidex/*` (idea: multidex split) | `:app` provides temp-dir + worker thread; `D8Dexer` runs in-process (like legacy `dx` did) with `minApi 26` default, `debug=true` for Run flow. Handle large apps: spill to files, not heap. JNI note: dex is **not** a C++ target (keep in Java/R8). | End-to-end compile→dex works on device for Hello-world; OOM guard test with 200 classes. |
| **26** | Resource/aapt seam | `aosp/*` (manifest-merger/sdklib/builder), `ProcessAndroidResourceTask`, `MergeManifestTask/MergeResourceTask/MergeAssetTask.java` | Define `core/aapt/AaptService.java` (`compileRes/mergeManifest/link`) + `core/apk/ApkBuilder.java` interfaces with `StubAapt/StubApkBuilder` defaults. Do NOT vendor `aosp/` (1265 files) — depend on `aapt2` binary or `com.android.tools.build:builder` Maven artifact later. Design `PackageRequest(manifest: String, resDir: File?, dexBytes, assets?)`. | Interfaces + stubs compile; `BuildAndRunCoordinator` calls them (still stub-fails cleanly); doc lists candidate `aapt2` sourcing options. |
| **27** | Real packaging + signing | `PackageApkTask.java`, `SignApkTask.java` (BouncyCastle idea), `JarTask/JarBuilder.java`, `ZipSigner` | Implement `ApkPackager implements Packager`: unsigned zip (java.util.zip, deterministic timestamps) → `zipalign` (or document skip) → sign via `apksig` (`com.android.tools.build:apksig`) debug key (generate once, store in app-private dir, never commit). Change `minSdk/targetSdk` to `int`. Return `File` (or `ParcelFileDescriptor`) not giant `byte[]`; keep `byte[]` overload for tests. | `assembleDebug`-style Hello APK installs via Task 13 path; `keystore.properties/*.jks` never committed (already gitignored). |

---

## Phase 7 — Run, logcat & console (make Run actually usable)

| # | Task | Legacy idea | Modern design | Acceptance |
|---|------|-------------|---------------|------------|
| **28** | Coordinator robustness | `ExecuteActivity.java`, `ConsoleEditText.java`, `IntegerQueue/ByteQueue` (stdin idea) | Fix leaks: `shutdown()`/lifecycle-aware scope (tie to Fragment `onDestroy`), per-run UUID listener map (no static overwrite), install timeout (e.g. 60 s → FAIL), `abandon()` on throw, emit `SAVE→COMPILE→DEX→PACKAGE→INSTALL→LAUNCH→DONE/FAILED` fully, derive APK filename + package from `PackageOptions`. Surface `RunResult` in bottom-sheet (scrollable, copy button), not Toast. Unit-test state machine with fakes. | Two rapid Runs don't clobber each other; cancel works; failed stages named correctly. |
| **29** | Logcat viewer | `androidlogcat/` Catlog fork (`LogcatActivity`, `LogcatReader/Abs/Multiple`, `LogLineAdapter`, `SaveLogHelper`, `FilterItem`) | **Reimplement streaming-first:** `LogcatRepository.observe(): Flow<LogLine>` (`Runtime.exec("logcat -v ...")` in `:app`), `LogcatViewModel` with `SearchCriteria` filter (`criteria.matches` already in `:core`), RecyclerView + ViewBinding, pause/resume, clear, share/export (`SaveLogHelper` idea), level colors. Reuse `core/log/*` parsers as-is (+ JNI port from Task 16). | Logcat screen filters by pid/tag/text, survives rotation, export shares a `.log` file. |

---

## Phase 8 — Language smarts (formatter, complete, templates)

| # | Task | Legacy idea | Modern design | Acceptance |
|---|------|-------------|---------------|------------|
| **30** | Formatter | `lib-google-java-format/Formatter.java`, `RemoveUnusedImports.java`, `ImportOrderer.java`, `JavaInputAstVisitor.java`, `app/.../editor/format/JavaFormatter,JavaIdeCodeFormatProvider.java` | `core/format/Formatter.java` interface (`format(Document): Document`) + `GoogleJavaFormatAdapter` (depend on `com.google.googlejavaformat:google-java-format` Maven, run off-main-thread) + `RemoveUnusedImports/OrderImports` as pure `String→String` use-cases. Editor menu "Format" + format-on-save setting. JNI candidate (pure string transform) — note in `PERF.md`. | Format Hello sample → google-java-format output; idempotent; large-file (<1 s or background + progress). |
| **31** | Autocomplete v1 | `JavaAutoCompleteProvider.java`, `JavaParser/JavaClassManager/JavaDexClassLoader.java`, `ExpressionResolver/StatementParser/TypeResolver.java`, `model/ClassDescription/MethodDescription/FieldDescription.java` | **Do NOT port regex resolvers** (README admits imperfect). v1 = keyword + in-file identifiers + imported-class short names via ECJ classpath scan, debounced, `Flow<List<CompletionItem>>` (`CompletionItem` immutable in `:core`), popup via `RecyclerView` anchored to cursor. v2 (later) = `javax.lang.model` / ECJ AST or LSP. Cache classpath index (DataStore). | Typing `Sys…` suggests `System`; `ArrayL…` suggests `ArrayList` with import insert; no UI freeze. |
| **32** | Problems panel (diagnostics UX) | `diagnostic/parser/*` + spans idea | `ProblemsFragment` (bottom sheet / tab): `List<Diagnostic>` from last compile, click → jump to line/col, error/warning icons, underline spans in editor (Task 21). Pure mapper `Diagnostic→Marker` in `:core`. | Compile errors clickable; editor shows squiggles; panel clears on success. |
| **33** | Templates + samples | `project/Template.java`, `JavaProject/AndroidAppProject/AndroidLibraryProject.java`, `DialogNewJavaProject/DialogNewAndroidProject.java`, `sample/AssetUtil,CodeProjectSample.java` | `core/template/ProjectTemplate.java` (immutable: `id/name/files: Map<path,content>`) + 3 built-ins (Console Java, Java Library/JAR, Android Hello). `NewProjectWizard` (2-step dialog, ViewBinding) writes via `FileRepository` (Task 18). Samples browser (read-only assets). | New → Console project in 2 taps; builds via Tasks 22–27. |

---

## Phase 9 — Settings, theme, polish, deferred big bets

| # | Task | Legacy idea | Modern design | Acceptance |
|---|------|-------------|---------------|------------|
| **34** | Settings | Legacy `preferences.*` in `common/` | `SettingsRepository` (DataStore Preferences): theme, font size, tab width, format-on-save, verbose, minApi. `SettingsFragment` (PreferenceCompat). Every setting actually wired (no dead toggles). | Changing font size / theme takes effect without restart (recreate). |
| **35** | Theme + icons | `art/` screenshots (idea) | `values/themes.xml` + `values-night/` Material3 dynamic color, theme-aware highlight palette (Task 21), launcher icon per `ICONS.md` (adaptive + monochrome). | Light/dark screenshots in PR; icon installs cleanly. |
| **36** | Tests + CI | None (legacy had none visible) | `core/src/test` JVM tests (tokenizer, StringOps, LogLine/SearchCriteria, ECJ compile, D8 dex when available, formatter idempotence) + `app/src/androidTest` (editor save/run smoke). GitHub Actions: `assembleDebug`, `testDebugUnitTest`, `lint`. | CI green on `modern-restructure`; coverage of `:core` pure fns >80%. |
| **37** | Layout preview spike (deferred) | `uidesigner/inflate/Inflater.java`, `DynamicLayoutInflator.java` | Time-boxed spike only: can we inflate a trivial `LinearLayout+TextView` XML in-app safely? Doc decision (build vs drop). No commitment. | `docs/LAYOUT_PREVIEW_SPIKE.md` with verdict. |
| **38** | Decompile seam (deferred) | `lib-decompiler/` Fernflower fork | `core/decompile/Decompiler.java` interface + stub only. No Fernflower vendoring yet. | Stub compiles; UI hidden behind flag. |
| **39** | Debugger research (deferred) | Missing in legacy (TODO), unused `ddmlib` | Doc only: JDWP/DAP options on-device, what `ExecuteActivity` streaming already gives us. No code. | `docs/DEBUGGER_NOTES.md` options + recommendation. |

---

## How to use

Reply with a number (e.g. `14`). One task per turn. Each implementation must:

- Keep `:core` pure + JNI-ready, `:app` ViewBinding + background work.
- Add/extend a JVM unit test in `:core` where logic changed.
- Update `docs/ARCHITECTURE.md` if a new seam/package was added.
- Mark the checkbox here when merged (maintainer edits this file).
