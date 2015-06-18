package com.atlassian.dfcheck.util;

import java.util.Map;
import java.util.Set;

import com.atlassian.dfcheck.core.Violation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ViolationUtil
{
    /**
     * Filters the violations by the given diff result to exclude any violation that doesn't
     * appear in the diff.
     *
     * @param violations the map of [File, Violations]
     * @param diff the diff between a source and target branch
     * @return violations that are only relevant to the diff
     */
    public static Map<String, Set<Violation>> filterViolationsByDiff(Map<String, Set<Violation>> violations, com.atlassian.dfcheck.core.Diff diff)
    {
        Map<String, Set<Violation>> filteredFileViolations = Maps.newHashMap();
        for (Map.Entry<String, Set<Violation>> entry : violations.entrySet())
        {
            Set<Violation> filteredViolations = Sets.newHashSet();
            for (Violation violation : entry.getValue())
            {
                if (diff.isLineEdited(violation.getFileName(), violation.getLineNumber()))
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
}
