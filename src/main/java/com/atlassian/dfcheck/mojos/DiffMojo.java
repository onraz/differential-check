package com.atlassian.dfcheck.mojos;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Maven Mojo that prints the effective edit diff between a source and target branch
 */
@Mojo(name = "diff")
public class DiffMojo extends AbstractDfMojo
{
    public static final String FILE_DIFF_SEPARATOR = ":";

    protected void processDiff(com.atlassian.dfcheck.core.Diff diff) throws MojoFailureException
    {
        StringBuilder sb = new StringBuilder("\n");
        for (Map.Entry<String, List<Range<Integer>>> entry : diff.getEdits().entrySet())
        {
            sb.append(entry.getKey() + FILE_DIFF_SEPARATOR + entry.getValue() + "\n");
        }
        getLog().info(sb.toString());
    }
}
