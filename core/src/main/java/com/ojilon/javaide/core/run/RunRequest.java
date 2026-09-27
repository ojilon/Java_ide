package com.ojilon.javaide.core.run;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ojilon.javaide.core.model.SourceFile;
import com.ojilon.javaide.core.packaging.PackageOptions;

import java.util.Objects;

/**
 * Immutable request to build and run a single source (or later a project).
 */
public final class RunRequest {

    private final SourceFile source;
    private final PackageOptions packageOptions;

    private RunRequest(@NonNull SourceFile source, @Nullable PackageOptions packageOptions) {
        this.source = source;
        this.packageOptions = packageOptions != null ? packageOptions : PackageOptions.defaults();
    }

    @NonNull
    public static RunRequest of(@NonNull SourceFile source) {
        return new RunRequest(Objects.requireNonNull(source), null);
    }

    @NonNull
    public static RunRequest of(@NonNull SourceFile source, @NonNull PackageOptions options) {
        return new RunRequest(source, options);
    }

    @NonNull
    public SourceFile getSource() { return source; }

    @NonNull
    public PackageOptions getPackageOptions() { return packageOptions; }
}
