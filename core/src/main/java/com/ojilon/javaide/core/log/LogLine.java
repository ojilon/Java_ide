package com.ojilon.javaide.core.log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Immutable representation of a single logcat line.
 * Pure / functional rewrite of the concept from a-java-ide (no mutable setters,
 * no Android framework dependency beyond annotations).
 *
 * Easy to port the parsing logic to C++ later and expose via JNI.
 */
public final class LogLine {

    private static final int TIMESTAMP_LENGTH = 19;

    private static final Pattern LOG_PATTERN = Pattern.compile(
            "(\\w)/([^(]+)\\(\\s*(\\d+)(?:\\*\\s*\\d+)?\\): "
    );

    private final int logLevel;
    private final String tag;
    private final String message;
    private final int processId;
    private final String timestamp;

    private LogLine(int logLevel,
                    @Nullable String tag,
                    @NonNull String message,
                    int processId,
                    @Nullable String timestamp) {
        this.logLevel = logLevel;
        this.tag = tag;
        this.message = message;
        this.processId = processId;
        this.timestamp = timestamp;
    }

    /** Parse a raw logcat line into an immutable LogLine. */
    @NonNull
    public static LogLine parse(@NonNull String originalLine) {
        String line = originalLine;
        String timestamp = null;
        int startIdx = 0;

        if (!line.isEmpty()
                && Character.isDigit(line.charAt(0))
                && line.length() >= TIMESTAMP_LENGTH) {
            timestamp = line.substring(0, TIMESTAMP_LENGTH - 1);
            startIdx = TIMESTAMP_LENGTH;
        }

        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.find(startIdx)) {
            int level = LogLevel.fromChar(matcher.group(1).charAt(0));
            String tag = matcher.group(2);
            int pid = Integer.parseInt(matcher.group(3));
            String msg = line.substring(matcher.end());
            return new LogLine(level, tag, msg, pid, timestamp);
        }

        // Fallback: treat whole line as message
        return new LogLine(LogLevel.UNKNOWN, null, line, -1, timestamp);
    }

    public int getLogLevel() { return logLevel; }

    @Nullable
    public String getTag() { return tag; }

    @NonNull
    public String getMessage() { return message; }

    public int getProcessId() { return processId; }

    @Nullable
    public String getTimestamp() { return timestamp; }

    /** Reconstruct a displayable original-style line. */
    @NonNull
    public String toOriginalLine() {
        if (logLevel == LogLevel.UNKNOWN) {
            return message;
        }
        StringBuilder sb = new StringBuilder();
        if (timestamp != null) {
            sb.append(timestamp).append(' ');
        }
        sb.append(LogLevel.toChar(logLevel))
          .append('/')
          .append(tag != null ? tag : "?")
          .append('(')
          .append(processId)
          .append("): ")
          .append(message);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogLine)) return false;
        LogLine that = (LogLine) o;
        return logLevel == that.logLevel
                && processId == that.processId
                && Objects.equals(tag, that.tag)
                && Objects.equals(message, that.message)
                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logLevel, tag, message, processId, timestamp);
    }

    @Override
    public String toString() {
        return toOriginalLine();
    }
}
