package com.ojilon.javaide.core.compile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable compiler options.
 * Keep this small and stable — future native backends can map it easily.
 */
public final class CompileOptions {

    private final String sourceVersion;
    private final String targetVersion;
    private final List<String> classpath;
    private final boolean verbose;

    private CompileOptions(
            @NonNull String sourceVersion,
            @NonNull String targetVersion,
            @NonNull List<String> classpath,
            boolean verbose) {
        this.sourceVersion = sourceVersion;
        this.targetVersion = targetVersion;
        this.classpath = classpath;
        this.verbose = verbose;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @NonNull
    public static CompileOptions defaults() {
        return builder().build();
    }

    @NonNull
    public String getSourceVersion() { return sourceVersion; }

    @NonNull
    public String getTargetVersion() { return targetVersion; }

    @NonNull
    public List<String> getClasspath() { return classpath; }

    public boolean isVerbose() { return verbose; }

    public static final class Builder {
        private String sourceVersion = "17";
        private String targetVersion = "17";
        private List<String> classpath = Collections.emptyList();
        private boolean verbose = false;

        @NonNull
        public Builder sourceVersion(@NonNull String v) {
            this.sourceVersion = Objects.requireNonNull(v);
            return this;
        }

        @NonNull
        public Builder targetVersion(@NonNull String v) {
            this.targetVersion = Objects.requireNonNull(v);
            return this;
        }

        @NonNull
        public Builder classpath(@Nullable List<String> cp) {
            this.classpath = cp == null
                    ? Collections.emptyList()
                    : Collections.unmodifiableList(cp);
            return this;
        }

        @NonNull
        public Builder verbose(boolean v) {
            this.verbose = v;
            return this;
        }

        @NonNull
        public CompileOptions build() {
            return new CompileOptions(sourceVersion, targetVersion, classpath, verbose);
        }
    }
}
