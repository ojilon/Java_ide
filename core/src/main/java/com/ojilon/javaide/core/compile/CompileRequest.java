package com.ojilon.javaide.core.compile;

import androidx.annotation.NonNull;

import com.ojilon.javaide.core.model.SourceFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable request to compile one or more source files.
 */
public final class CompileRequest {

    private final List<SourceFile> sources;
    private final CompileOptions options;

    private CompileRequest(@NonNull List<SourceFile> sources, @NonNull CompileOptions options) {
        this.sources = sources;
        this.options = options;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /** Convenience: compile a single in-memory source with default options. */
    @NonNull
    public static CompileRequest of(@NonNull SourceFile source) {
        return builder().addSource(source).build();
    }

    @NonNull
    public List<SourceFile> getSources() { return sources; }

    @NonNull
    public CompileOptions getOptions() { return options; }

    public static final class Builder {
        private final List<SourceFile> sources = new ArrayList<>();
        private CompileOptions options = CompileOptions.defaults();

        @NonNull
        public Builder addSource(@NonNull SourceFile file) {
            sources.add(Objects.requireNonNull(file));
            return this;
        }

        @NonNull
        public Builder sources(@NonNull List<SourceFile> files) {
            sources.clear();
            sources.addAll(files);
            return this;
        }

        @NonNull
        public Builder options(@NonNull CompileOptions opts) {
            this.options = Objects.requireNonNull(opts);
            return this;
        }

        @NonNull
        public CompileRequest build() {
            if (sources.isEmpty()) {
                throw new IllegalStateException("CompileRequest requires at least one source");
            }
            return new CompileRequest(
                    Collections.unmodifiableList(new ArrayList<>(sources)),
                    options
            );
        }
    }
}
