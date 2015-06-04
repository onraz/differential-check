package com.atlassian.dfcheck.diff;

import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;

public class Diff
{
    private final List<DiffEntry> diffEntryList;

    protected Diff(List<DiffEntry> diffEntryList)
    {
        this.diffEntryList = diffEntryList;
    }

    /**
     * Determines if a file has an edited line that was either added or changed but not deleted.
     *
     * @param fileName the file
     * @param lineNumber the line number
     * @return true if the line has been changed
     */
    public boolean isLineEdited(String fileName, String lineNumber)
    {
        return false;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Size: " + diffEntryList.size());
        for (DiffEntry diffEntry : diffEntryList)
        {
            sb.append("Diff>" + diffEntry);
        }
        return sb.toString();
    }
}
