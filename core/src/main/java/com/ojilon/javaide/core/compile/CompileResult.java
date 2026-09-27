package com.ojilon.javaide.core.compile;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable result of a compilation.
 */
public final class CompileResult {

    private final boolean success;
    private final List<Diagnostic> diagnostics;
    private final List<CompiledClass> classes;

    private CompileResult(
            boolean success,
            @NonNull List<Diagnostic> diagnostics,
            @NonNull List<CompiledClass> classes) {
        this.success = success;
        this.diagnostics = diagnostics;
        this.classes = classes;
    }

    @NonNull
    public static CompileResult success(@NonNull List<CompiledClass> classes,
                                        @NonNull List<Diagnostic> diagnostics) {
        return new CompileResult(
                true,
                Collections.unmodifiableList(new ArrayList<>(diagnostics)),
                Collections.unmodifiableList(new ArrayList<>(classes))
        );
    }

    @NonNull
    public static CompileResult failure(@NonNull List<Diagnostic> diagnostics) {
        return new CompileResult(
                false,
                Collections.unmodifiableList(new ArrayList<>(diagnostics)),
                Collections.emptyList()
        );
    }

    @NonNull
    public static CompileResult notImplemented() {
        return failure(Collections.singletonList(
                Diagnostic.info("Compiler backend not yet implemented (task 11)")
        ));
    }

    public boolean isSuccess() { return success; }

    @NonNull
    public List<Diagnostic> getDiagnostics() { return diagnostics; }

    @NonNull
    public List<CompiledClass> getClasses() { return classes; }

    public boolean hasErrors() {
        for (Diagnostic d : diagnostics) {
            if (d.getSeverity() == DiagnosticSeverity.ERROR) return true;
        }
        return false;
    }
}
