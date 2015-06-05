package com.atlassian.dfcheck.diff;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;
import org.eclipse.jgit.diff.DiffEntry;

import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class Diff
{
    private final Map<String, List<Range<Integer>>> editDiffs;
//    private final List<DiffEntry> diffEntryList;

    protected Diff(List<DiffEntry> diffEntryList)
    {
        this.editDiffs = FileEditDiffCollector.collectEdits(diffEntryList);
//        this.diffEntryList = diffEntryList;
    }

    /**
     * Determines if a file has an edited line that was either added or changed but not deleted.
     *
     * @param fileName the file
     * @param line the line number
     * @return true if the line has been changed
     */
    public boolean isLineEdited(String fileName, String line)
    {
        if (editDiffs.containsKey(fileName))
        {
            Integer lineNumber = toInt(line);
            for (Range<Integer> range : editDiffs.get(fileName))
            {
                if (range.contains(lineNumber))
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("\n");
        for (Map.Entry<String, List<Range<Integer>>> entry : editDiffs.entrySet())
        {
            sb.append(entry.getKey() + "-->" + entry.getValue() + "\n");
        }
        return sb.toString();
    }

    //    @Override
//    public String toString()
//    {
//        StringBuilder sb = new StringBuilder();
//        Map<String, List<Range<Integer>>> edits = FileEditDiffCollector.collectEdits(diffEntryList);
//        for (Map.Entry<String, List<Range<Integer>>> entry : edits.entrySet())
//        {
//            sb.append(entry.getKey() + "-->" + entry.getValue() + "\n");
//        }
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        DiffFormatter formatter = new DiffFormatter(out);
//        formatter.setRepository(RepositoryUtil.getLocalRepository());
//        formatter.setContext(0);
//
//        for (DiffEntry diffEntry : diffEntryList)
//        {
//            try
//            {
//                formatter.format(diffEntry);
//            }
//            catch (IOException e)
//            {
//                throw new RuntimeException("Error formatting diff entry: " + diffEntry, e);
//            }
//        }
//        return sb.toString() + "\n\n" + new String(out.toByteArray());
//    }
}
