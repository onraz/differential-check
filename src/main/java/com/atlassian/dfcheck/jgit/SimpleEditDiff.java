package com.atlassian.dfcheck.jgit;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;
import org.eclipse.jgit.diff.DiffEntry;

import static org.apache.commons.lang3.math.NumberUtils.toInt;

/**
 * A Diff that only retains edits (i.e. modifications and additions, but not deletions)
 */
public class SimpleEditDiff implements com.atlassian.dfcheck.core.Diff
{
    private final Map<String, List<Range<Integer>>> edits;

    protected SimpleEditDiff(List<DiffEntry> diffEntryList)
    {
        this.edits = FileEditDiffCollector.collectEdits(diffEntryList);
    }

    public boolean isLineEdited(String fileName, String line)
    {
        if (edits.containsKey(fileName))
        {
            Integer lineNumber = toInt(line);
            for (Range<Integer> range : edits.get(fileName))
            {
                if (range.contains(lineNumber))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public Map<String, List<Range<Integer>>> getEdits()
    {
        return edits;
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
