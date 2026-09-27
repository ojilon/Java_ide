package com.ojilon.javaide.core.compile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Immutable compiler diagnostic (error / warning / note).
 * Line and column are 1-based; -1 means unknown.
 */
public final class Diagnostic {

    private final DiagnosticSeverity severity;
    private final String message;
    private final String fileName;
    private final int line;
    private final int column;

    public Diagnostic(
            @NonNull DiagnosticSeverity severity,
            @NonNull String message,
            @Nullable String fileName,
            int line,
            int column) {
        this.severity = severity;
        this.message = message;
        this.fileName = fileName;
        this.line = line;
        this.column = column;
    }

    @NonNull
    public static Diagnostic error(@NonNull String message, @Nullable String fileName, int line, int column) {
        return new Diagnostic(DiagnosticSeverity.ERROR, message, fileName, line, column);
    }

    @NonNull
    public static Diagnostic warning(@NonNull String message, @Nullable String fileName, int line, int column) {
        return new Diagnostic(DiagnosticSeverity.WARNING, message, fileName, line, column);
    }

    @NonNull
    public static Diagnostic info(@NonNull String message) {
        return new Diagnostic(DiagnosticSeverity.INFO, message, null, -1, -1);
    }

    @NonNull
    public DiagnosticSeverity getSeverity() { return severity; }

    @NonNull
    public String getMessage() { return message; }

    @Nullable
    public String getFileName() { return fileName; }

    public int getLine() { return line; }

    public int getColumn() { return column; }

    @Override
    public String toString() {
        String loc = fileName != null
                ? fileName + ":" + line + ":" + column + ": "
                : "";
        return severity + ": " + loc + message;
    }
}
