package com.ojilon.javaide.core.syntax;

/**
 * Token categories produced by the pure Java tokenizer.
 * Designed so the same classification can later be done in C++ and returned via JNI.
 */
public enum TokenType {
    KEYWORD,
    STRING,
    COMMENT,
    NUMBER,
    IDENTIFIER,
    OTHER
}
