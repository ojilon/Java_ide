package com.ojilon.javaide.core.log;

/**
 * Immutable log-level constants (functional-friendly).
 * Mirrors android.util.Log values without depending on the Android framework in pure logic.
 */
public final class LogLevel {

    public static final int VERBOSE = 2;
    public static final int DEBUG   = 3;
    public static final int INFO    = 4;
    public static final int WARN    = 5;
    public static final int ERROR   = 6;
    public static final int ASSERT  = 7; // WTF / F
    public static final int UNKNOWN = -1;

    private LogLevel() {}

    public static int fromChar(char c) {
        switch (c) {
            case 'V': return VERBOSE;
            case 'D': return DEBUG;
            case 'I': return INFO;
            case 'W': return WARN;
            case 'E': return ERROR;
            case 'F': case 'A': return ASSERT;
            default:  return UNKNOWN;
        }
    }

    public static char toChar(int level) {
        switch (level) {
            case VERBOSE: return 'V';
            case DEBUG:   return 'D';
            case INFO:    return 'I';
            case WARN:    return 'W';
            case ERROR:   return 'E';
            case ASSERT:  return 'F';
            default:      return ' ';
        }
    }
}
