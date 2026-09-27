package com.ojilon.javaide.core.dex;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Immutable options for dexing.
 * Mapped later to d8/r8 flags.
 */
public final class DexOptions {

    private final int minApiLevel;
    private final boolean debug;
    private final boolean verbose;

    private DexOptions(int minApiLevel, boolean debug, boolean verbose) {
        this.minApiLevel = minApiLevel;
        this.debug = debug;
        this.verbose = verbose;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @NonNull
    public static DexOptions defaults() {
        return builder().build();
    }

    public int getMinApiLevel() { return minApiLevel; }

    public boolean isDebug() { return debug; }

    public boolean isVerbose() { return verbose; }

    public static final class Builder {
        private int minApiLevel = 26;
        private boolean debug = true;
        private boolean verbose = false;

        @NonNull
        public Builder minApiLevel(int level) {
            this.minApiLevel = level;
            return this;
        }

        @NonNull
        public Builder debug(boolean d) {
            this.debug = d;
            return this;
        }

        @NonNull
        public Builder verbose(boolean v) {
            this.verbose = v;
            return this;
        }

        @NonNull
        public DexOptions build() {
            return new DexOptions(minApiLevel, debug, verbose);
        }
    }
}
