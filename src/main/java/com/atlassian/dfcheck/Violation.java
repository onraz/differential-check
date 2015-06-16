package com.atlassian.dfcheck;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Violation
{
    private final String fileName;
    private final String lineNumber;
    private final String message;
    private final String source;

    public Violation(String fileName, String lineNumber, String message, String source)
    {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.message = message;
        this.source = source;
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
        return new ToStringBuilder(this)
                .append("fileName", fileName)
                .append("lineNumber", lineNumber)
                .append("message", message)
                .append("source", source)
                .toString();
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
                Objects.equals(source, violation.source);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(fileName, lineNumber, message, source);
    }
}
