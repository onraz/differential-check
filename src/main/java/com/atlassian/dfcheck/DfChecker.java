package com.atlassian.dfcheck;

import java.util.Collections;
import java.util.Set;

import com.atlassian.dfcheck.diff.Diff;
import com.atlassian.dfcheck.diff.DiffCalculator;
import com.atlassian.dfcheck.util.RepositoryUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "diff")
public class DfChecker extends AbstractMojo
{
    @Parameter( property = "source")
    private String source;

    @Parameter( property = "target", defaultValue = "refs/heads/master" )
    private String target;

    @Parameter( property = "checkstyleReport", required = true)
    private String checkstyleReport;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (StringUtils.isBlank(source))
        {
            source = RepositoryUtil.getLocalBranch();
        }
        getLog().info("Computing diff between source: " + source + " and target: " + target);
        DiffCalculator diffCalculator = new DiffCalculator(source, target);
        Diff diff = diffCalculator.calculate();
        getLog().info(diff.toString());
    }

    public static void main(String[] args) {
        final Set<Object> set = Collections.emptySet();
        set.add("123");

    }
}
