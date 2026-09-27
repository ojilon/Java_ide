package com.ojilon.javaide.core.packaging;

import androidx.annotation.NonNull;

/**
 * Placeholder until a real APK builder is wired.
 */
public final class StubPackager implements Packager {

    @NonNull
    @Override
    public PackageResult packageApk(@NonNull PackageRequest request) {
        return PackageResult.notImplemented();
    }
}
