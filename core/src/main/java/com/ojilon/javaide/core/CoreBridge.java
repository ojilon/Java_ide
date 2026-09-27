package com.ojilon.javaide.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.ojilon.javaide.core.functional.StringOps;
import com.ojilon.javaide.core.log.LogLine;
import com.ojilon.javaide.core.log.SearchCriteria;
import com.ojilon.javaide.core.model.DocumentStore;
import com.ojilon.javaide.core.model.SourceFile;
import com.ojilon.javaide.core.syntax.JavaTokenizer;
import com.ojilon.javaide.core.syntax.Token;

import java.util.List;

/**
 * Narrow bridge from Android UI (OOP) into the functional / JNI-ready core.
 */
public final class CoreBridge {

    private CoreBridge() {}

    @NonNull
    public static String hello() {
        return "Java IDE core ready (functional layer)";
    }

    // ── Document / editor API ────────────────────────────────────────────────

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

    public static void closeDocument(@NonNull String id) {
        DocumentStore.getInstance().close(id);
    }

    // ── Syntax (task 9) ──────────────────────────────────────────────────────

    @NonNull
    public static List<Token> tokenizeJava(@NonNull String source) {
        return JavaTokenizer.tokenize(source);
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
