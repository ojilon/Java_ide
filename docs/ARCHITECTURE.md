# Architecture

## Guiding principles

1. **No legacy** from a-java-ide.
2. **XML + Java**.
3. **`:app`** = OOP UI · **`:core`** = functional + JNI-ready.

## Build pipeline (tasks 10–12)

```
SourceFile(s)
      │
      ▼
CoreBridge.compile()  →  EcjCompiler  →  CompileResult (class bytes)
      │
      ▼
CoreBridge.dex()      →  Dexer (StubDexer for now)  →  DexResult (dex bytes)
      │
      ▼
CoreBridge.packageApk() → Packager (StubPackager) → PackageResult (apk bytes)
```

- All steps use immutable request/result types.
- Backends are swappable (`setCompiler` / `setDexer` / `setPackager`).
- Real d8/r8 and APK building plug in later without UI changes.

## Packages

```
core/
  compile/     # ECJ frontend
  dex/         # DexRequest/Result, Dexer, StubDexer
  packaging/   # PackageRequest/Result, Packager, StubPackager
  model/
  syntax/
  ...
```
