package com.atlassian.dfcheck;

import java.io.File;

import com.atlassian.dfcheck.checkstyle.CheckstyleDfPlugin;
import com.atlassian.dfcheck.diff.Diff;
import com.atlassian.dfcheck.diff.DiffCalculator;
import com.atlassian.dfcheck.util.RepositoryUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "check")
public class DfChecker extends AbstractMojo
{
    @Parameter( property = "dfcheck.source")
    private String source;

    @Parameter( property = "dfcheck.target", defaultValue = "master" )
    private String target;

    @Parameter( property = "dfcheck.failOnViolation", defaultValue = "true" )
    private Boolean failOnViolation;

    @Parameter( property = "dfcheck.showDiff", defaultValue = "false" )
    private Boolean showDiff;

    @Parameter( property = "dfcheck.checkstyleReport", defaultValue = "${project.build.directory}/checkstyle-result.xml")
    private File checkstyleReport;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (StringUtils.isBlank(source))
        {
            source = RepositoryUtil.getLocalBranch();
        }

        getLog().info("==========================================================================");
        getLog().info("Differential Check between source: [" + source + "] and target: [" + target + "]");
        getLog().info("==========================================================================");

        Diff diff = new DiffCalculator(source, target).calculate();

        DfPlugin checkstylePlugin = new CheckstyleDfPlugin();

        DfCheck diffCheck = new DfCheck(diff, checkstylePlugin.parse(checkstyleReport));


        if (diffCheck.hasViolations())
        {
            getLog().error("Violations detected : " + diffCheck.getViolations().size());
            if (failOnViolation)
            {
                throw new MojoFailureException("Differential Check detected violations, please see output");
            }
        }
        else
        {
            getLog().error("No violations detected by Differential Check.");
        }

        if (showDiff)
        {
            getLog().info(diff.toString());
        }
    }
}
