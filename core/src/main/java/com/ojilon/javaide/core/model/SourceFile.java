package com.ojilon.javaide.core.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable representation of an open source file.
 * Functional style — no setters; create a new instance to “modify”.
 */
public final class SourceFile {

    private final String id;
    private final String name;
    private final String content;

    private SourceFile(@NonNull String id, @NonNull String name, @NonNull String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    @NonNull
    public static SourceFile create(@NonNull String name, @NonNull String content) {
        return new SourceFile(UUID.randomUUID().toString(), name, content);
    }

    @NonNull
    public static SourceFile createNew() {
        return create("Untitled.java", "public class Untitled {\n    \n}\n");
    }

    /** Return a new SourceFile with updated content (same id & name). */
    @NonNull
    public SourceFile withContent(@NonNull String newContent) {
        return new SourceFile(id, name, newContent);
    }

    /** Return a new SourceFile with a new name (same id & content). */
    @NonNull
    public SourceFile withName(@NonNull String newName) {
        return new SourceFile(id, newName, content);
    }

    @NonNull
    public String getId() { return id; }

    @NonNull
    public String getName() { return name; }

    @NonNull
    public String getContent() { return content; }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof SourceFile)) return false;
        SourceFile that = (SourceFile) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SourceFile{id='" + id + "', name='" + name + "'}";
    }
}
