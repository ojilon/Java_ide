package com.ojilon.javaide.core.run;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ojilon.javaide.core.compile.Diagnostic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable outcome of a build-and-run attempt.
 */
public final class RunResult {

    private final boolean success;
    private final RunStage failedStage;
    private final List<Diagnostic> diagnostics;
    private final String message;

    private RunResult(
            boolean success,
            @Nullable RunStage failedStage,
            @NonNull List<Diagnostic> diagnostics,
            @NonNull String message) {
        this.success = success;
        this.failedStage = failedStage;
        this.diagnostics = diagnostics;
        this.message = message;
    }

    @NonNull
    public static RunResult ok(@NonNull String message) {
        return new RunResult(true, null, Collections.emptyList(), message);
    }

    @NonNull
    public static RunResult fail(@NonNull RunStage stage, @NonNull String message,
                                 @NonNull List<Diagnostic> diagnostics) {
        return new RunResult(
                false,
                stage,
                Collections.unmodifiableList(new ArrayList<>(diagnostics)),
                message
        );
    }

    @NonNull
    public static RunResult fail(@NonNull RunStage stage, @NonNull String message) {
        return fail(stage, message, Collections.emptyList());
    }

    public boolean isSuccess() { return success; }

    @Nullable
    public RunStage getFailedStage() { return failedStage; }

    @NonNull
    public List<Diagnostic> getDiagnostics() { return diagnostics; }

    @NonNull
    public String getMessage() { return message; }
}
