# Architecture

## Guiding principles

1. **No legacy** — nothing from the old AGP 3.x / AOSP / JDK 1.7 tree is copied as-is.
2. **XML + Java only** (for now).
3. **Clear split**:
   - `:app` → Android UI, Activities, Fragments, adapters → **OOP**
   - `:core` → pure logic, parsing, models, future native → **functional style** + JNI seams
4. Everything that might later move to C++ lives behind narrow, immutable interfaces in `:core`.

## Module map

```
Java_ide/
├── app/                          # Android application (OOP)
│   └── src/main/java/com/ojilon/javaide/
│       └── ui/
│           ├── MainActivity
│           ├── editor/           # EditorFragment + layouts
│           └── project/
├── core/                         # Library (functional + JNI-ready)
│   └── src/main/java/com/ojilon/javaide/core/
│       ├── CoreBridge            # narrow API for UI
│       ├── functional/           # pure helpers (StringOps, …)
│       ├── log/                  # LogLine, SearchCriteria, LogLevel
│       ├── model/                # SourceFile, DocumentStore
│       └── jni/                  # NativeBridge (future)
└── docs/
    ├── ROADMAP.md
    └── ARCHITECTURE.md
```

## Editor & document flow (tasks 6–7)

```
EditorFragment (OOP)
      │
      │  getText() / setText()
      ▼
CoreBridge.openNew() / open(name, content) / save(id, content)
      │
      ▼
DocumentStore (in-memory, functional style)
      │
      ▼
SourceFile (immutable: id, name, content)
```

- UI never mutates a `SourceFile` directly; it always goes through `CoreBridge` / `DocumentStore`.
- Later a real filesystem or Storage Access Framework implementation can replace the in-memory store without touching the UI.

## Data flow (general)

```
UI (OOP)  ──calls──►  CoreBridge  ──uses──►  functional / model / log classes
                              │
                              └── (future) NativeBridge → libjavaide_core.so
```

## Adding new features

- UI widgets / screens → `app/.../ui/...`
- Pure algorithms, parsers, models → `core/...` (prefer immutable data + static methods)
- Anything performance-critical that should become C++ → put the Java side in `core` first, keep the interface stable, then implement native later.
