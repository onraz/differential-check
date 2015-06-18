package com.atlassian.dfcheck.core;

import java.util.Objects;

/**
 * Represents a violation of a condition, as a result of some check execution.
 */
public class Violation
{
    private final String fileName;
    private final String lineNumber;
    private final String message;
    private final String source;
    private final String plugin;

    public Violation(String fileName, String lineNumber, String message, String source, String plugin)
    {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.message = message;
        this.source = source;
        this.plugin = plugin;
    }

    public String getFileName()
    {
        return fileName;
    }

    public String getLineNumber()
    {
        return lineNumber;
    }

    public String getMessage()
    {
        return message;
    }

    public String getSource()
    {
        return source;
    }

    @Override
    public String toString()
    {
        return "[" + lineNumber + "] " + message + " [" + plugin + ": " + source + "]";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Violation violation = (Violation) o;
        return Objects.equals(fileName, violation.fileName) &&
                Objects.equals(lineNumber, violation.lineNumber) &&
                Objects.equals(message, violation.message) &&
                Objects.equals(source, violation.source) &&
                Objects.equals(plugin, violation.plugin);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(fileName, lineNumber, message, source, plugin);
    }
}
