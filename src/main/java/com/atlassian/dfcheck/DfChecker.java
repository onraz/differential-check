package com.atlassian.dfcheck;

import java.io.File;
import java.util.Map;
import java.util.Set;

import com.atlassian.dfcheck.plugins.DfPlugin;
import com.atlassian.dfcheck.plugins.checkstyle.CheckstyleDfPlugin;
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

    @Parameter( property = "dfcheck.checkstyle", defaultValue = "true" )
    private Boolean checkstyleEnabled;

    @Parameter( property = "dfcheck.checkstyle-severity", defaultValue = "error" )
    private Boolean checkstyleSeverity;

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

        getLog().info("================================================================================");
        getLog().info("Differential Check between source: [" + source + "] and target: [" + target + "]");
        getLog().info("================================================================================");

        Diff diff = new DiffCalculator(source, target).calculate();

        DfPlugin checkstylePlugin = new CheckstyleDfPlugin();

        DfCheckResult diffCheck = new DfCheckResult(diff, checkstylePlugin.parse(checkstyleReport));


        if (diffCheck.hasViolations())
        {
            processViolations(diffCheck);
        }
        else
        {
            getLog().info("No violations detected by Differential Check.");
        }

        if (showDiff)
        {
            getLog().info(diff.toString());
        }
    }

    private void processViolations(DfCheckResult diffCheck) throws MojoFailureException
    {
        Map<String, Set<Violation>> violations = diffCheck.getViolations();
        getLog().error("Violations were introduced in branch: " + source);

        for (Map.Entry<String, Set<Violation>> entry : violations.entrySet())
        {
            getLog().error("------------------------------------------------------------------------");
            for (Violation violation : entry.getValue())
            {
                getLog().error("+ " + violation.getFileName() + ":" + violation.getLineNumber() + ": " + violation.getMessage() + " [" + violation.getSource() + "]");
            }
        }

        if (failOnViolation)
        {
            throw new MojoFailureException("\nDifferential Check detected violations, please see output");
        }
    }
}
