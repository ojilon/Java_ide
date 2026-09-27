package com.ojilon.javaide.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.ojilon.javaide.core.functional.StringOps;
import com.ojilon.javaide.core.log.LogLine;
import com.ojilon.javaide.core.log.SearchCriteria;
import com.ojilon.javaide.core.model.DocumentStore;
import com.ojilon.javaide.core.model.SourceFile;

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

    // ── Document / editor API (tasks 6–7) ─────────────────────────────────────

    @NonNull
    public static SourceFile openNewDocument() {
        return DocumentStore.getInstance().openNew();
    }

    @NonNull
    public static SourceFile openDocument(@NonNull String name, @NonNull String content) {
        return DocumentStore.getInstance().open(name, content);
    }

    @Nullable
    public static SourceFile saveDocument(@NonNull String id, @NonNull String content) {
        return DocumentStore.getInstance().save(id, content);
    }

    @Nullable
    public static SourceFile getDocument(@NonNull String id) {
        return DocumentStore.getInstance().get(id);
    }

    @NonNull
    public static List<SourceFile> listDocuments() {
        return DocumentStore.getInstance().list();
    }

    // ── Log utilities ────────────────────────────────────────────────────────

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
    public static String trimOrEmpty(@Nullable String input) {
        return StringOps.trimOrEmpty(input);
    }
}
