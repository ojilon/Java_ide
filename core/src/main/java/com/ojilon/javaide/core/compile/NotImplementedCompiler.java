package com.ojilon.javaide.core.compile;

import androidx.annotation.NonNull;

/**
 * Placeholder backend used until a real compiler (task 11) is wired in.
 */
public final class NotImplementedCompiler implements Compiler {

    @NonNull
    @Override
    public CompileResult compile(@NonNull CompileRequest request) {
        return CompileResult.notImplemented();
    }
}
