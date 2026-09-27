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
│           ├── editor/
│           └── project/
├── core/                         # Library (functional + JNI-ready)
│   └── src/main/java/com/ojilon/javaide/core/
│       ├── CoreBridge            # narrow API for UI
│       ├── functional/           # pure helpers
│       ├── log/                  # LogLine, SearchCriteria, LogLevel
│       └── jni/                  # NativeBridge (future)
└── docs/
    ├── ROADMAP.md
    └── ARCHITECTURE.md
```

## Data flow (current)

```
UI (OOP)  ──calls──►  CoreBridge  ──uses──►  functional / log classes
                              │
                              └── (future) NativeBridge → libjavaide_core.so
```

## Adding new features

- UI widgets / screens → `app/.../ui/...`
- Pure algorithms, parsers, models → `core/...` (prefer immutable data + static methods)
- Anything performance-critical that should become C++ → put the Java side in `core` first, keep the interface stable, then implement native later.
