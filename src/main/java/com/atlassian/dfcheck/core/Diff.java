package com.atlassian.dfcheck.core;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;

/**
 * Represents the result of a diff computation between two branches in a git tree,
 * only for the lines that were modified (i.e. added or changed, but not removed)
 */
public interface Diff
{
    /**
     * Determines if a file has an edited line that was either added or changed but not deleted.
     *
     * @param fileName the file
     * @param line the line number
     * @return true if the line has been changed
     */
    boolean isLineEdited(String fileName, String line);

    /**
     * @return only the part of the diff that corresponds to edits
     */
    Map<String, List<Range<Integer>>> getEdits();
}
