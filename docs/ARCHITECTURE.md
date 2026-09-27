# Architecture

## Build & run pipeline (tasks 10–13)

```
Editor "Run"
      │
      ▼
BuildAndRunCoordinator (app, background thread)
      │
      ├─ CoreBridge.compile()     → EcjCompiler
      ├─ CoreBridge.dex()         → StubDexer (for now)
      ├─ CoreBridge.packageApk()  → StubPackager (for now)
      ├─ ApkInstaller.install()   → PackageInstaller session
      └─ ApkInstaller.launch()    → launch intent
```

- Core stays free of PackageInstaller; install/launch live in `:app`.
- Stage progress is reported via `RunStage` + callbacks on the main thread.
- When real dex/package backends land, the same coordinator works end-to-end.

## Packages

```
core/run/          # RunRequest, RunResult, RunStage
app/run/           # ApkInstaller, InstallResultReceiver, BuildAndRunCoordinator
```
