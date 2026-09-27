package com.ojilon.javaide.core.syntax;

import androidx.annotation.NonNull;

/**
 * Immutable token: type + [start, end) character offsets into the source.
 */
public final class Token {

    private final TokenType type;
    private final int start;
    private final int end;

    public Token(@NonNull TokenType type, int start, int end) {
        this.type = type;
        this.start = start;
        this.end = end;
    }

    @NonNull
    public TokenType getType() { return type; }

    public int getStart() { return start; }

    public int getEnd() { return end; }

    @Override
    public String toString() {
        return type + "[" + start + "," + end + ")";
    }
}
