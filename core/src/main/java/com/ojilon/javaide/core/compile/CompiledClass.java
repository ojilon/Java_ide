package com.ojilon.javaide.core.compile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Immutable result of compiling a single class.
 * Holds the binary name and the class-file bytes (when available).
 */
public final class CompiledClass {

    private final String binaryName;
    private final byte[] bytes;

    public CompiledClass(@NonNull String binaryName, @Nullable byte[] bytes) {
        this.binaryName = binaryName;
        this.bytes = bytes != null ? bytes.clone() : null;
    }

    @NonNull
    public String getBinaryName() { return binaryName; }

    /** May be null if the backend only produced files on disk. */
    @Nullable
    public byte[] getBytes() {
        return bytes != null ? bytes.clone() : null;
    }
}
