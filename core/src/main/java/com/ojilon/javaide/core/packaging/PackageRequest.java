package com.ojilon.javaide.core.packaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Immutable request to package DEX (+ optional resources) into an APK.
 */
public final class PackageRequest {

    private final byte[] dexBytes;
    private final PackageOptions options;

    private PackageRequest(@NonNull byte[] dexBytes, @NonNull PackageOptions options) {
        this.dexBytes = dexBytes.clone();
        this.options = options;
    }

    @NonNull
    public static PackageRequest of(@NonNull byte[] dexBytes, @Nullable PackageOptions options) {
        return new PackageRequest(
                Objects.requireNonNull(dexBytes),
                options != null ? options : PackageOptions.defaults()
        );
    }

    @NonNull
    public byte[] getDexBytes() { return dexBytes.clone(); }

    @NonNull
    public PackageOptions getOptions() { return options; }
}
