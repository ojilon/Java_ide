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
├── app/
│   └── ui/
│       ├── MainActivity
│       └── editor/
│           ├── EditorFragment          # tab host
│           ├── EditorPagerAdapter
│           ├── EditorPageFragment      # one tab
│           └── SyntaxHighlighter       # applies spans (UI)
├── core/
│   ├── CoreBridge
│   ├── functional/
│   ├── log/
│   ├── model/          # SourceFile, DocumentStore
│   ├── syntax/         # Token, TokenType, JavaTokenizer (pure)
│   └── jni/
└── docs/
```

## Editor & highlighting flow

```
EditorFragment (tabs)
      │
      ▼
EditorPageFragment  ──text──►  SyntaxHighlighter  ──calls──►  CoreBridge.tokenizeJava()
                                      │                              │
                                      │                              ▼
                                      │                       JavaTokenizer (pure)
                                      ▼
                               Spannable with ColorSpans
```

- Tokenizer is pure Java in `:core` → easy future C++ / JNI port.
- Coloring (Android-specific) stays in the UI layer.

## Adding new features

- UI → `app/.../ui/...`
- Pure algorithms / models → `core/...`
- Hot paths that should become native → keep stable Java API in `core` first.
