package com.atlassian.dfcheck;

import java.io.File;
import java.util.Map;
import java.util.Set;

public interface DfPlugin
{
    Map<String, Set<Violation>> parse(File checkstyleReport);
}
