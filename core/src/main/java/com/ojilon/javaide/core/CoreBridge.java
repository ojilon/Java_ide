package com.ojilon.javaide.core;

import androidx.annotation.NonNull;
import com.ojilon.javaide.core.functional.StringOps;
import com.ojilon.javaide.core.log.LogLine;
import com.ojilon.javaide.core.log.SearchCriteria;

import java.util.List;

/**
 * Narrow bridge from Android UI (OOP) into the functional / JNI-ready core.
 *
 * Design goals:
 * - Pure / functional style where possible (static methods, immutable data).
 * - Easy to replace implementation with native (C++) via JNI later.
 * - No Android UI types leak into this layer.
 */
public final class CoreBridge {

    private CoreBridge() {
        // no instances
    }

    /** Simple health-check / entry point. Later this can become a JNI call. */
    @NonNull
    public static String hello() {
        return "Java IDE core ready (functional layer)";
    }

    // ── Log utilities (modernized from a-java-ide logcat logic) ──────────────

    @NonNull
    public static LogLine parseLogLine(@NonNull String rawLine) {
        return LogLine.parse(rawLine);
    }

    @NonNull
    public static SearchCriteria parseSearch(@NonNull CharSequence query) {
        return SearchCriteria.of(query);
    }

    public static boolean matches(@NonNull SearchCriteria criteria, @NonNull LogLine line) {
        return criteria.matches(line);
    }

    // ── String helpers ───────────────────────────────────────────────────────

    @NonNull
    public static String trimOrEmpty(@androidx.annotation.Nullable String input) {
        return StringOps.trimOrEmpty(input);
    }

    // Future: load native library and declare native methods here, e.g.
    // static {
    //     System.loadLibrary("javaide_core");
    // }
    // public static native String nativeHello();
}
