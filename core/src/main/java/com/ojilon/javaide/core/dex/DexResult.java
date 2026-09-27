package com.ojilon.javaide.core.dex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ojilon.javaide.core.compile.Diagnostic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable result of a dexing step.
 */
public final class DexResult {

    private final boolean success;
    private final List<Diagnostic> diagnostics;
    private final byte[] dexBytes;

    private DexResult(boolean success, @NonNull List<Diagnostic> diagnostics, @Nullable byte[] dexBytes) {
        this.success = success;
        this.diagnostics = diagnostics;
        this.dexBytes = dexBytes != null ? dexBytes.clone() : null;
    }

    @NonNull
    public static DexResult success(@NonNull byte[] dexBytes, @NonNull List<Diagnostic> diagnostics) {
        return new DexResult(
                true,
                Collections.unmodifiableList(new ArrayList<>(diagnostics)),
                dexBytes
        );
    }

    @NonNull
    public static DexResult failure(@NonNull List<Diagnostic> diagnostics) {
        return new DexResult(
                false,
                Collections.unmodifiableList(new ArrayList<>(diagnostics)),
                null
        );
    }

    @NonNull
    public static DexResult notImplemented() {
        return failure(Collections.singletonList(
                Diagnostic.info("Dexer backend not yet implemented (wire d8/r8 later)")
        ));
    }

    public boolean isSuccess() { return success; }

    @NonNull
    public List<Diagnostic> getDiagnostics() { return diagnostics; }

    /** DEX file bytes, or null on failure. */
    @Nullable
    public byte[] getDexBytes() {
        return dexBytes != null ? dexBytes.clone() : null;
    }
}
