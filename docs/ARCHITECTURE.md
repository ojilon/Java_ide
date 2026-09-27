# Architecture

## Guiding principles

1. **No legacy** — nothing from the old AGP 3.x / AOSP / JDK 1.7 tree is copied as-is.
2. **XML + Java only** (for now).
3. **Clear split**:
   - `:app` → Android UI → **OOP**
   - `:core` → pure logic / models / future native → **functional style** + JNI seams
4. Hot paths that may move to C++ stay behind narrow, immutable interfaces in `:core`.

## Module map

```
core/
  compile/          # Task 10 – pure compile API
    CompileRequest
    CompileOptions
    Diagnostic / DiagnosticSeverity
    CompiledClass
    CompileResult
    Compiler (interface)
    NotImplementedCompiler (stub)
  model/            # SourceFile, DocumentStore
  syntax/           # JavaTokenizer, Token, TokenType
  functional/
  log/
  jni/
  CoreBridge
```

## Compile flow (task 10)

```
UI / future BuildAction
        │
        ▼
CoreBridge.compile(CompileRequest)
        │
        ▼
Compiler.compile(request)     ← swappable backend
        │
        ▼
CompileResult (success? diagnostics, class bytes)
```

- Default backend: `NotImplementedCompiler` (returns a clear info diagnostic).
- Task 11 will supply a real `Compiler` implementation and call `CoreBridge.setCompiler(...)`.

## Adding new features

- UI → `app/.../ui/...`
- Pure algorithms / models → `core/...`
- Native candidates → keep stable Java API first, then implement in C++ behind the same interface.
