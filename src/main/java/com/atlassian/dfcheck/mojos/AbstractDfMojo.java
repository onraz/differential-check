package com.atlassian.dfcheck.mojos;

import com.atlassian.dfcheck.diff.DiffCalculator;
import com.atlassian.dfcheck.util.RepositoryUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Abstract Mojo that computes diff between a source and target branch.
 * Subclasses provide implementation that process the computed diff in different ways.
 */
public abstract class AbstractDfMojo extends AbstractMojo
{
    @Parameter( property = "dfcheck.source")
    protected String source;

    @Parameter( property = "dfcheck.target", defaultValue = "master" )
    protected String target;

    @Parameter( property = "dfcheck.skip", defaultValue = "false" )
    protected boolean skip;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (StringUtils.isBlank(source))
        {
            source = RepositoryUtil.getLocalBranch();
        }

        getLog().info("================================================================================");
        getLog().info("Differential Check between source: [" + source + "] and target: [" + target + "]");
        getLog().info("================================================================================");

        com.atlassian.dfcheck.core.Diff diff = new DiffCalculator(source, target).calculate();
        if (!skip)
        {
            processDiff(diff);
        }
    }

    protected abstract void processDiff(com.atlassian.dfcheck.core.Diff diff) throws MojoFailureException;
}
