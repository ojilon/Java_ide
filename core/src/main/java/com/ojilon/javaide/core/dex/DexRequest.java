package com.ojilon.javaide.core.dex;

import androidx.annotation.NonNull;

import com.ojilon.javaide.core.compile.CompiledClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable request to convert class files into DEX.
 */
public final class DexRequest {

    private final List<CompiledClass> classes;
    private final DexOptions options;

    private DexRequest(@NonNull List<CompiledClass> classes, @NonNull DexOptions options) {
        this.classes = classes;
        this.options = options;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @NonNull
    public static DexRequest of(@NonNull List<CompiledClass> classes) {
        return builder().classes(classes).build();
    }

    @NonNull
    public List<CompiledClass> getClasses() { return classes; }

    @NonNull
    public DexOptions getOptions() { return options; }

    public static final class Builder {
        private List<CompiledClass> classes = Collections.emptyList();
        private DexOptions options = DexOptions.defaults();

        @NonNull
        public Builder classes(@NonNull List<CompiledClass> c) {
            this.classes = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(c)));
            return this;
        }

        @NonNull
        public Builder options(@NonNull DexOptions o) {
            this.options = Objects.requireNonNull(o);
            return this;
        }

        @NonNull
        public DexRequest build() {
            if (classes.isEmpty()) {
                throw new IllegalStateException("DexRequest requires at least one class");
            }
            return new DexRequest(classes, options);
        }
    }
}
