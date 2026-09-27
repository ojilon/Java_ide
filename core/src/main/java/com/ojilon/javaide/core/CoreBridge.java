package com.ojilon.javaide.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.ojilon.javaide.core.compile.CompileRequest;
import com.ojilon.javaide.core.compile.CompileResult;
import com.ojilon.javaide.core.compile.Compiler;
import com.ojilon.javaide.core.compile.EcjCompiler;
import com.ojilon.javaide.core.dex.DexRequest;
import com.ojilon.javaide.core.dex.DexResult;
import com.ojilon.javaide.core.dex.Dexer;
import com.ojilon.javaide.core.dex.StubDexer;
import com.ojilon.javaide.core.functional.StringOps;
import com.ojilon.javaide.core.log.LogLine;
import com.ojilon.javaide.core.log.SearchCriteria;
import com.ojilon.javaide.core.model.DocumentStore;
import com.ojilon.javaide.core.model.SourceFile;
import com.ojilon.javaide.core.packaging.PackageRequest;
import com.ojilon.javaide.core.packaging.PackageResult;
import com.ojilon.javaide.core.packaging.Packager;
import com.ojilon.javaide.core.packaging.StubPackager;
import com.ojilon.javaide.core.syntax.JavaTokenizer;
import com.ojilon.javaide.core.syntax.Token;

import java.util.List;

/**
 * Narrow bridge from Android UI (OOP) into the functional / JNI-ready core.
 */
public final class CoreBridge {

    private static volatile Compiler compiler = new EcjCompiler();
    private static volatile Dexer dexer = new StubDexer();
    private static volatile Packager packager = new StubPackager();

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

    // ── Syntax ───────────────────────────────────────────────────────────────

    @NonNull
    public static List<Token> tokenizeJava(@NonNull String source) {
        return JavaTokenizer.tokenize(source);
    }

    // ── Compile API ──────────────────────────────────────────────────────────

    public static void setCompiler(@NonNull Compiler newCompiler) {
        compiler = newCompiler;
    }

    @NonNull
    public static Compiler getCompiler() {
        return compiler;
    }

    @NonNull
    public static CompileResult compile(@NonNull CompileRequest request) {
        return compiler.compile(request);
    }

    @NonNull
    public static CompileResult compile(@NonNull SourceFile source) {
        return compile(CompileRequest.of(source));
    }

    // ── Dex API (task 12) ────────────────────────────────────────────────────

    public static void setDexer(@NonNull Dexer newDexer) {
        dexer = newDexer;
    }

    @NonNull
    public static Dexer getDexer() {
        return dexer;
    }

    @NonNull
    public static DexResult dex(@NonNull DexRequest request) {
        return dexer.dex(request);
    }

    // ── Package API (task 12) ────────────────────────────────────────────────

    public static void setPackager(@NonNull Packager newPackager) {
        packager = newPackager;
    }

    @NonNull
    public static Packager getPackager() {
        return packager;
    }

    @NonNull
    public static PackageResult packageApk(@NonNull PackageRequest request) {
        return packager.packageApk(request);
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
