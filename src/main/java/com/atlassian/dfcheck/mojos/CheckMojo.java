package com.atlassian.dfcheck.mojos;

import java.io.File;
import java.util.Map;
import java.util.Set;

import com.atlassian.dfcheck.core.Violation;
import com.atlassian.dfcheck.plugins.checkstyle.CheckstyleDfPlugin;
import com.atlassian.dfcheck.util.ViolationUtil;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Maven Mojo that computes violations from a diff between source and target branches.
 */
@Mojo(name = "dfcheck", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class CheckMojo extends AbstractDfMojo
{
    @Parameter( property = "dfcheck.checkstyle", defaultValue = "true" )
    private boolean checkstyleEnabled;

    @Parameter( property = "dfcheck.checkstyle-severity", defaultValue = "error" )
    private String checkstyleSeverity;

    @Parameter( property = "dfcheck.checkstyleReport", defaultValue = "${project.build.directory}/checkstyle-result.xml")
    private File checkstyleReport;

    @Parameter( property = "dfcheck.failOnViolation", defaultValue = "true" )
    private boolean failOnViolation;

    //
    // TODO Define options for output as JSON, HTML, XML e.t.c for tools integration
    //

    protected void processDiff(com.atlassian.dfcheck.core.Diff diff) throws MojoFailureException
    {
        Map<String, Set<Violation>> violations = new CheckstyleDfPlugin().parse(checkstyleReport);
        final Map<String, Set<Violation>> filteredViolations = ViolationUtil.filterViolationsByDiff(violations, diff);

        if (!filteredViolations.isEmpty())
        {
            int totalViolations = processViolations(filteredViolations);

            if (failOnViolation)
            {
                throw new MojoFailureException("\nDifferential Check detected " + totalViolations + " violations, please see output");
            }
        }
        else
        {
            getLog().info("No violations detected by Differential Check.");
        }
    }

    private int processViolations(Map<String, Set<Violation>> violations) throws MojoFailureException
    {
        getLog().error("Violations were introduced in branch: " + source);
        getLog().error("------------------------------------------------------------------------");

        int totalViolations = 0;
        for (Map.Entry<String, Set<Violation>> entry : violations.entrySet())
        {
            getLog().error(entry.getKey() + ":");
            for (Violation violation : entry.getValue())
            {
                getLog().error("  " + violation);
                totalViolations++;
            }
            getLog().error("");
        }

        return totalViolations;
    }
}
