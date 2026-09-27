package com.ojilon.javaide.core.packaging;

import androidx.annotation.NonNull;

/**
 * APK packaging backend contract.
 */
public interface Packager {

    @NonNull
    PackageResult packageApk(@NonNull PackageRequest request);
}
