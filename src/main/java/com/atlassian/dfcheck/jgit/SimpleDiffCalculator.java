package com.atlassian.dfcheck.jgit;

import java.io.IOException;
import java.util.List;

import com.atlassian.dfcheck.core.Diff;
import com.atlassian.dfcheck.util.RepositoryUtil;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;

/**
 * Calculates and constructs a {@link SimpleEditDiff} between a source and target branch
 * in a git tree
 */
public class SimpleDiffCalculator implements com.atlassian.dfcheck.core.DiffCalculator
{
    private final String sourceBranchName;
    private final String targetBranchName;
    private final boolean readUncommited;

    public SimpleDiffCalculator(String sourceBranchName, String targetBranchName, boolean readUncommited)
    {
        this.sourceBranchName = sourceBranchName;
        this.targetBranchName = targetBranchName;
        this.readUncommited = readUncommited;
    }

    public Diff calculate()
    {
        try
        {
            Repository repository = RepositoryUtil.getLocalRepository();

            // the diff works on TreeIterators, we prepare two for the two branches
            AbstractTreeIterator sourceTree = readUncommited ? new FileTreeIterator(repository)
                                                             : prepareTreeParser(repository, sourceBranchName);
            AbstractTreeIterator targetTree = prepareTreeParser(repository, targetBranchName);

            List<DiffEntry> diffEntries = new Git(repository).diff()
                                            .setOldTree(targetTree)
                                            .setNewTree(sourceTree)
                                            .call();
            return new SimpleEditDiff(diffEntries);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not parse branches for calculating diff", e);
        }
        catch (GitAPIException e)
        {
            throw new RuntimeException("Could not calculate diff due to error", e);
        }
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws IOException
    {
        // from the commit we can build the tree which allows us to construct the TreeParser
        Ref head = repository.getRef(ref);
        RevWalk walk = new RevWalk(repository);
        RevCommit commit = walk.parseCommit(head.getObjectId());
        RevTree tree = walk.parseTree(commit.getTree().getId());

        CanonicalTreeParser treeParser = new CanonicalTreeParser();
        ObjectReader reader = repository.newObjectReader();
        treeParser.reset(reader, tree.getId());
        reader.release();

        walk.dispose();
        return treeParser;
    }
}
