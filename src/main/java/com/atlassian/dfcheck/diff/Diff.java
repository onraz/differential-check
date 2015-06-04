package com.atlassian.dfcheck.diff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.atlassian.dfcheck.util.RepositoryUtil;

import org.apache.commons.lang3.Range;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;

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
        Map<String, List<Range<Integer>>> edits = FileEditDiffCollector.collectEdits(diffEntryList);
        for (Map.Entry<String, List<Range<Integer>>> entry : edits.entrySet())
        {
            sb.append(entry.getKey() + "-->" + entry.getValue() + "\n");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter formatter = new DiffFormatter(out);
        formatter.setRepository(RepositoryUtil.getLocalRepository());
        formatter.setContext(0);

        for (DiffEntry diffEntry : diffEntryList)
        {
            try
            {
                formatter.format(diffEntry);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Error formatting diff entry: " + diffEntry, e);
            }
        }
        return sb.toString() + "\n\n" + new String(out.toByteArray());
    }

    ;
}
