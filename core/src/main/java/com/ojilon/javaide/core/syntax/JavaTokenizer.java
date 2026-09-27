package com.ojilon.javaide.core.syntax;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pure functional Java tokenizer for basic syntax highlighting.
 * No Android dependencies beyond annotations.
 * Easy to re-implement in C++ later and call via JNI.
 */
public final class JavaTokenizer {

    private static final Set<String> KEYWORDS;

    static {
        Set<String> kw = new HashSet<>();
        String[] words = {
                "abstract", "assert", "boolean", "break", "byte", "case", "catch",
                "char", "class", "const", "continue", "default", "do", "double",
                "else", "enum", "extends", "final", "finally", "float", "for",
                "goto", "if", "implements", "import", "instanceof", "int",
                "interface", "long", "native", "new", "package", "private",
                "protected", "public", "return", "short", "static", "strictfp",
                "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while", "true", "false", "null"
        };
        Collections.addAll(kw, words);
        KEYWORDS = Collections.unmodifiableSet(kw);
    }

    private JavaTokenizer() {}

    /**
     * Tokenize the entire source. Returns an immutable list of tokens covering
     * interesting regions (keywords, strings, comments, numbers, identifiers).
     * Gaps between tokens are left as plain text (OTHER is not emitted for whitespace).
     */
    @NonNull
    public static List<Token> tokenize(@NonNull String source) {
        List<Token> tokens = new ArrayList<>();
        int n = source.length();
        int i = 0;

        while (i < n) {
            char c = source.charAt(i);

            // Whitespace — skip
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // Line comment //
            if (c == '/' && i + 1 < n && source.charAt(i + 1) == '/') {
                int start = i;
                i += 2;
                while (i < n && source.charAt(i) != '\n') i++;
                tokens.add(new Token(TokenType.COMMENT, start, i));
                continue;
            }

            // Block comment /* ... */
            if (c == '/' && i + 1 < n && source.charAt(i + 1) == '*') {
                int start = i;
                i += 2;
                while (i + 1 < n && !(source.charAt(i) == '*' && source.charAt(i + 1) == '/')) {
                    i++;
                }
                if (i + 1 < n) i += 2; // consume */
                else i = n;
                tokens.add(new Token(TokenType.COMMENT, start, i));
                continue;
            }

            // String literal "..."
            if (c == '"') {
                int start = i;
                i++; // skip opening "
                while (i < n) {
                    char ch = source.charAt(i);
                    if (ch == '\\' && i + 1 < n) {
                        i += 2; // escape
                        continue;
                    }
                    if (ch == '"') {
                        i++; // closing "
                        break;
                    }
                    i++;
                }
                tokens.add(new Token(TokenType.STRING, start, i));
                continue;
            }

            // Character literal '...' (treat like string for coloring)
            if (c == '\'') {
                int start = i;
                i++;
                while (i < n) {
                    char ch = source.charAt(i);
                    if (ch == '\\' && i + 1 < n) {
                        i += 2;
                        continue;
                    }
                    if (ch == '\'') {
                        i++;
                        break;
                    }
                    i++;
                }
                tokens.add(new Token(TokenType.STRING, start, i));
                continue;
            }

            // Number
            if (Character.isDigit(c)) {
                int start = i;
                i++;
                while (i < n) {
                    char ch = source.charAt(i);
                    if (Character.isDigit(ch) || ch == '.' || ch == '_' ||
                            ch == 'x' || ch == 'X' || ch == 'l' || ch == 'L' ||
                            ch == 'f' || ch == 'F' || ch == 'd' || ch == 'D') {
                        i++;
                    } else {
                        break;
                    }
                }
                tokens.add(new Token(TokenType.NUMBER, start, i));
                continue;
            }

            // Identifier or keyword
            if (Character.isJavaIdentifierStart(c)) {
                int start = i;
                i++;
                while (i < n && Character.isJavaIdentifierPart(source.charAt(i))) {
                    i++;
                }
                String word = source.substring(start, i);
                TokenType type = KEYWORDS.contains(word) ? TokenType.KEYWORD : TokenType.IDENTIFIER;
                tokens.add(new Token(type, start, i));
                continue;
            }

            // Single symbol — skip (no special coloring)
            i++;
        }

        return Collections.unmodifiableList(tokens);
    }
}
