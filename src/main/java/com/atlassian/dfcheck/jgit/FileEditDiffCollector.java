package com.atlassian.dfcheck.jgit;

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

/**
 * Collects only the diff results that were introduced as per edits
 * Internal use only by {@link SimpleEditDiff}
 */
class FileEditDiffCollector
{
    /**
     * The change types that are considered to be edited (ie. lines that are either added or modified, but not deleted)
     */
    private static final Set<DiffEntry.ChangeType> EDITED_CHANGETYPE = Sets.immutableEnumSet(DiffEntry.ChangeType.ADD, DiffEntry.ChangeType.MODIFY);


    /**
     * The meaning of the file name can differ depending on the semantic meaning of the diff
     * <ul>
     *      <li><i> file add    </i>:   always the file being created   </li>
     *      <li><i> file modify </i>:   always the existing path        </li>
     *      <li><i> file delete </i>:   always <code>/dev/null</code>   </li>
     *      <li><i> file copy   </i>:   destination file the copy ends up at</li>
     *      <li><i> file rename </i>:   destination file the rename ends up at</li>
     * </ul>
     *
     * @param diffEntries diff be
     * @return  only the diff results that were introduced as per edits
    */
    public static Map<String, List<Range<Integer>>> collectEdits(List<DiffEntry> diffEntries)
    {
        // Get the root directory of this repository
        String rootDir = RepositoryUtil.getLocalRepository().getDirectory().getParent();

        // A map of file names -> edited lines
        Map<String, List<Range<Integer>>> fileEdits = Maps.newHashMap();

        for (DiffEntry diffEntry : diffEntries)
        {
            if (EDITED_CHANGETYPE.contains(diffEntry.getChangeType()))
            {
                fileEdits.put(rootDir + "/" + diffEntry.getNewPath(), collectEditedLines(diffEntry));
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
