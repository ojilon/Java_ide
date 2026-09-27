package com.ojilon.javaide.ui.editor;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;

import com.ojilon.javaide.core.CoreBridge;
import com.ojilon.javaide.core.syntax.Token;
import com.ojilon.javaide.core.syntax.TokenType;

import java.util.List;

/**
 * Applies syntax coloring to text using the pure tokenizer from :core.
 * Lives in the UI layer (OOP) because it deals with Android Spannable.
 */
public final class SyntaxHighlighter {

    // Simple fixed palette (can later become theme-aware)
    private static final int COLOR_KEYWORD   = Color.parseColor("#CC7832"); // orange
    private static final int COLOR_STRING    = Color.parseColor("#6A8759"); // green
    private static final int COLOR_COMMENT   = Color.parseColor("#808080"); // gray
    private static final int COLOR_NUMBER    = Color.parseColor("#6897BB"); // blue
    private static final int COLOR_IDENTIFIER = Color.parseColor("#A9B7C6"); // light

    private SyntaxHighlighter() {}

    @NonNull
    public static Spannable highlight(@NonNull String source) {
        SpannableStringBuilder builder = new SpannableStringBuilder(source);
        List<Token> tokens = CoreBridge.tokenizeJava(source);

        for (Token token : tokens) {
            int color = colorFor(token.getType());
            if (color != 0) {
                int start = Math.max(0, token.getStart());
                int end = Math.min(builder.length(), token.getEnd());
                if (start < end) {
                    builder.setSpan(
                            new ForegroundColorSpan(color),
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
            }
        }
        return builder;
    }

    private static int colorFor(@NonNull TokenType type) {
        switch (type) {
            case KEYWORD:    return COLOR_KEYWORD;
            case STRING:     return COLOR_STRING;
            case COMMENT:    return COLOR_COMMENT;
            case NUMBER:     return COLOR_NUMBER;
            case IDENTIFIER: return COLOR_IDENTIFIER;
            default:         return 0;
        }
    }
}
