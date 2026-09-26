package com.ojilon.javaide.core.functional;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Pure functional helpers for string processing.
 * No mutable state, no Android dependencies beyond annotations.
 * Designed so the same logic can later be reimplemented in C++ and called via JNI.
 */
public final class StringOps {

    private StringOps() {}

    @NonNull
    public static String trimOrEmpty(@Nullable String input) {
        return input == null ? "" : input.trim();
    }

    public static boolean isBlank(@Nullable String input) {
        return input == null || input.trim().isEmpty();
    }

    @NonNull
    public static String nullToEmpty(@Nullable String input) {
        return input == null ? "" : input;
    }
}
