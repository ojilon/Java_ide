package com.ojilon.javaide.core.log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.ojilon.javaide.core.functional.StringOps;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Immutable search criteria for filtering LogLines.
 * Functional rewrite of the concept from a-java-ide.
 *
 * Supports keywords:
 *   pid:123
 *   tag:MyTag   or   tag:"My Tag"
 * plus free-text search.
 */
public final class SearchCriteria {

    private static final Pattern PID_PATTERN =
            Pattern.compile("pid:(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TAG_PATTERN =
            Pattern.compile("tag:(\"[^\"]+\"|\\S+)", Pattern.CASE_INSENSITIVE);

    private final int pid;
    private final String tag;
    private final String searchText;
    private final int searchTextAsInt;

    private SearchCriteria(int pid, @Nullable String tag, @NonNull String searchText) {
        this.pid = pid;
        this.tag = tag;
        this.searchText = searchText;
        int asInt = -1;
        try {
            asInt = Integer.parseInt(searchText);
        } catch (NumberFormatException ignored) {}
        this.searchTextAsInt = asInt;
    }

    @NonNull
    public static SearchCriteria of(@Nullable CharSequence inputQuery) {
        StringBuilder query = new StringBuilder(StringOps.nullToEmpty(
                inputQuery == null ? null : inputQuery.toString()));

        int pid = -1;
        Matcher pidMatcher = PID_PATTERN.matcher(query);
        if (pidMatcher.find()) {
            try {
                pid = Integer.parseInt(pidMatcher.group(1));
                query.replace(pidMatcher.start(), pidMatcher.end(), "");
            } catch (NumberFormatException ignored) {}
        }

        String tag = null;
        Matcher tagMatcher = TAG_PATTERN.matcher(query);
        if (tagMatcher.find()) {
            tag = tagMatcher.group(1);
            if (tag.startsWith("\"") && tag.endsWith("\"") && tag.length() >= 2) {
                tag = tag.substring(1, tag.length() - 1);
            }
            query.replace(tagMatcher.start(), tagMatcher.end(), "");
        }

        String searchText = query.toString().trim();
        return new SearchCriteria(pid, tag, searchText);
    }

    public boolean isEmpty() {
        return pid == -1 && StringOps.isBlank(tag) && StringOps.isBlank(searchText);
    }

    public boolean matches(@NonNull LogLine line) {
        if (pid != -1 && line.getProcessId() != pid) {
            return false;
        }
        if (!StringOps.isBlank(tag)
                && !StringOps.containsIgnoreCase(line.getTag(), tag)) {
            return false;
        }
        if (StringOps.isBlank(searchText)) {
            return true;
        }
        if (searchTextAsInt != -1 && searchTextAsInt == line.getProcessId()) {
            return true;
        }
        return StringOps.containsIgnoreCase(line.getTag(), searchText)
                || StringOps.containsIgnoreCase(line.getMessage(), searchText);
    }

    public int getPid() { return pid; }

    @Nullable
    public String getTag() { return tag; }

    @NonNull
    public String getSearchText() { return searchText; }
}
