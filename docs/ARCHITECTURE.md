# Architecture

## Guiding principles

1. **No legacy** from a-java-ide.
2. **XML + Java**.
3. **`:app`** = OOP UI · **`:core`** = functional + JNI-ready.
4. Hot paths stay behind stable interfaces in `:core`.

## Compile pipeline (tasks 10–11)

```
UI / Build action
      │
      ▼
CoreBridge.compile(CompileRequest)
      │
      ▼
EcjCompiler  ──uses──►  org.eclipse.jdt:ecj (BatchCompiler)
      │
      ▼
CompileResult (diagnostics + class bytes)
```

- Default backend is now **EcjCompiler** (task 11).
- Swap with `CoreBridge.setCompiler(...)` if needed.
- Pure-Java sources work out of the box. Android API compilation needs `android.jar` on the classpath (future).

## Module map (excerpt)

```
core/
  compile/
    Compiler / EcjCompiler / NotImplementedCompiler
    CompileRequest / CompileOptions / CompileResult
    Diagnostic / CompiledClass
  model/
  syntax/
  ...
```
