package com.ojilon.javaide.core.packaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Immutable options for APK packaging.
 */
public final class PackageOptions {

    private final String applicationId;
    private final int versionCode;
    private final String versionName;
    private final String minSdk;
    private final String targetSdk;

    private PackageOptions(
            @NonNull String applicationId,
            int versionCode,
            @NonNull String versionName,
            @NonNull String minSdk,
            @NonNull String targetSdk) {
        this.applicationId = applicationId;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.minSdk = minSdk;
        this.targetSdk = targetSdk;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @NonNull
    public static PackageOptions defaults() {
        return builder().build();
    }

    @NonNull public String getApplicationId() { return applicationId; }
    public int getVersionCode() { return versionCode; }
    @NonNull public String getVersionName() { return versionName; }
    @NonNull public String getMinSdk() { return minSdk; }
    @NonNull public String getTargetSdk() { return targetSdk; }

    public static final class Builder {
        private String applicationId = "com.example.generated";
        private int versionCode = 1;
        private String versionName = "1.0";
        private String minSdk = "26";
        private String targetSdk = "34";

        @NonNull public Builder applicationId(@NonNull String id) { this.applicationId = id; return this; }
        @NonNull public Builder versionCode(int c) { this.versionCode = c; return this; }
        @NonNull public Builder versionName(@NonNull String n) { this.versionName = n; return this; }
        @NonNull public Builder minSdk(@NonNull String v) { this.minSdk = v; return this; }
        @NonNull public Builder targetSdk(@NonNull String v) { this.targetSdk = v; return this; }

        @NonNull
        public PackageOptions build() {
            return new PackageOptions(applicationId, versionCode, versionName, minSdk, targetSdk);
        }
    }
}
