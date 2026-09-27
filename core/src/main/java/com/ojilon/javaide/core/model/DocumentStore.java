package com.ojilon.javaide.core.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple in-memory document store (functional-leaning).
 * All public methods are synchronized for basic thread safety.
 * Later this can be replaced by a filesystem-backed or SAF-backed implementation
 * without changing the UI layer.
 */
public final class DocumentStore {

    private static final DocumentStore INSTANCE = new DocumentStore();

    private final Map<String, SourceFile> files = new LinkedHashMap<>();

    private DocumentStore() {}

    @NonNull
    public static DocumentStore getInstance() {
        return INSTANCE;
    }

    /** Create a new empty document and return it. */
    @NonNull
    public synchronized SourceFile openNew() {
        SourceFile file = SourceFile.createNew();
        files.put(file.getId(), file);
        return file;
    }

    /** Open (or create) a document with the given name and content. */
    @NonNull
    public synchronized SourceFile open(@NonNull String name, @NonNull String content) {
        SourceFile file = SourceFile.create(name, content);
        files.put(file.getId(), file);
        return file;
    }

    /**
     * Save content for an existing document.
     * @return the updated SourceFile, or null if the id was unknown.
     */
    @Nullable
    public synchronized SourceFile save(@NonNull String id, @NonNull String content) {
        SourceFile existing = files.get(id);
        if (existing == null) {
            return null;
        }
        SourceFile updated = existing.withContent(content);
        files.put(id, updated);
        return updated;
    }

    @Nullable
    public synchronized SourceFile get(@NonNull String id) {
        return files.get(id);
    }

    @NonNull
    public synchronized List<SourceFile> list() {
        return Collections.unmodifiableList(new ArrayList<>(files.values()));
    }

    public synchronized void close(@NonNull String id) {
        files.remove(id);
    }

    public synchronized void clear() {
        files.clear();
    }
}
