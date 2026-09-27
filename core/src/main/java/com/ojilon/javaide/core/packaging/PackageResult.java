package com.ojilon.javaide.core.packaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ojilon.javaide.core.compile.Diagnostic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable result of packaging an APK.
 */
public final class PackageResult {

    private final boolean success;
    private final List<Diagnostic> diagnostics;
    private final byte[] apkBytes;

    private PackageResult(boolean success, @NonNull List<Diagnostic> diagnostics, @Nullable byte[] apkBytes) {
        this.success = success;
        this.diagnostics = diagnostics;
        this.apkBytes = apkBytes != null ? apkBytes.clone() : null;
    }

    @NonNull
    public static PackageResult success(@NonNull byte[] apkBytes, @NonNull List<Diagnostic> diagnostics) {
        return new PackageResult(
                true,
                Collections.unmodifiableList(new ArrayList<>(diagnostics)),
                apkBytes
        );
    }

    @NonNull
    public static PackageResult failure(@NonNull List<Diagnostic> diagnostics) {
        return new PackageResult(
                false,
                Collections.unmodifiableList(new ArrayList<>(diagnostics)),
                null
        );
    }

    @NonNull
    public static PackageResult notImplemented() {
        return failure(Collections.singletonList(
                Diagnostic.info("Packager backend not yet implemented")
        ));
    }

    public boolean isSuccess() { return success; }

    @NonNull
    public List<Diagnostic> getDiagnostics() { return diagnostics; }

    @Nullable
    public byte[] getApkBytes() {
        return apkBytes != null ? apkBytes.clone() : null;
    }
}
