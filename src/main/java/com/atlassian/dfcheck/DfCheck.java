package com.atlassian.dfcheck;

import java.util.Map;
import java.util.Set;

import com.atlassian.dfcheck.diff.Diff;

import com.google.common.collect.ImmutableMap;

public class DfCheck
{
    private final Map<String, Set<Violation>> fileViolations;
    private final Diff editDiff;

    public DfCheck(Diff editDiff, Map<String, Set<Violation>> violations)
    {
        this.editDiff = editDiff;
        this.fileViolations = ImmutableMap.copyOf(violations);
    }

    public boolean hasViolations()
    {
        return !fileViolations.isEmpty();
    }

    public Map<String, Set<Violation>> getViolations()
    {
        return fileViolations;
    }

    public Diff getEditDiff()
    {
        return editDiff;
    }
}
