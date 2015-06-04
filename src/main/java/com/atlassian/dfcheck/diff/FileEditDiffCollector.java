package com.atlassian.dfcheck.diff;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atlassian.dfcheck.util.RepositoryUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;

import org.apache.commons.lang3.Range;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;

class FileEditDiffCollector
{
    /**
     * The change types that are considered to be edited (ie. lines that are either added or modified, but not deleted)
     */
    private static final Set<DiffEntry.ChangeType> EDITED_CHANGETYPE = Sets.immutableEnumSet(DiffEntry.ChangeType.ADD, DiffEntry.ChangeType.MODIFY);


    public static Map<String, List<Range<Integer>>> collectEdits(List<DiffEntry> diffEntries)
    {
        // A map of file names -> edited lines
        Map<String, List<Range<Integer>>> fileEdits = Maps.newHashMap();

        for (DiffEntry diffEntry : diffEntries)
        {
            if (EDITED_CHANGETYPE.contains(diffEntry.getChangeType()))
            {
                fileEdits.put(diffEntry.getNewPath(), collectEditedLines(diffEntry));
            }
        }

        return ImmutableMap.copyOf(fileEdits);
    }

    private static List<Range<Integer>> collectEditedLines(DiffEntry diffEntry)
    {
        return  new DiffWalker(ByteStreams.nullOutputStream()).walkEdits(diffEntry);
    }

    private static class DiffWalker extends DiffFormatter
    {
        private List<Range<Integer>> editRanges;

        /**
         * Create a new formatter with a default level of context.
         *
         * @param out the stream the formatter will write line data to. This stream
         *            should have buffering arranged by the caller, as many small
         *            writes are performed to it.
         */
        public DiffWalker(OutputStream out)
        {
            super(out);
            this.setRepository(RepositoryUtil.getLocalRepository());
            this.setContext(0);
        }

        public List<Range<Integer>> walkEdits(DiffEntry diffEntry)
        {
            editRanges = Lists.newArrayList();

            try
            {
                super.format(diffEntry);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Error formatting diff entry: " + diffEntry, e);
            }

            return ImmutableList.copyOf(editRanges);
        }

        @Override
        protected void writeHunkHeader(int aStartLine, int aEndLine, int bStartLine, int bEndLine) throws IOException
        {
            // ignore deleted code, bEndLine is zero for deletions
            if (bEndLine > 0)
            {
                editRanges.add(Range.between(bStartLine + 1, bEndLine + 1));
            }

            super.writeHunkHeader(aStartLine, aEndLine, bStartLine, bEndLine);
        }

    }
}
