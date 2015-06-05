package com.atlassian.dfcheck;

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
}
