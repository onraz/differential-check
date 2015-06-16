package com.atlassian.dfcheck;

import java.util.Map;
import java.util.Set;

import com.atlassian.dfcheck.diff.Diff;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DfCheckResult
{
    private final Map<String, Set<Violation>> fileViolations;
    private final Diff editDiff;

    public DfCheckResult(Diff editDiff, Map<String, Set<Violation>> violations)
    {
        this.editDiff = editDiff;
        this.fileViolations = filterViolations(violations);
    }

    private Map<String, Set<Violation>> filterViolations(Map<String, Set<Violation>> violations)
    {
        Map<String, Set<Violation>> filteredFileViolations = Maps.newHashMap();
        for (Map.Entry<String, Set<Violation>> entry : violations.entrySet())
        {
            Set<Violation> filteredViolations = Sets.newHashSet();
            for (Violation violation : entry.getValue())
            {
                if (editDiff.isLineEdited(violation.getFileName(), violation.getLineNumber()))
                {
                    filteredViolations.add(violation);
                }
            }
            if (!filteredViolations.isEmpty())
            {
                filteredFileViolations.put(entry.getKey(), filteredViolations);
            }
        }
        return filteredFileViolations;
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
