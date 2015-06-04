package com.atlassian.dfcheck.util;

import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class RepositoryUtil
{
    // the static final initialisation is guaranteed to be thread safe
    private static final Repository LOCAL_REPOSITORY = readLocalRepository();

    public static Repository getLocalRepository()
    {
        return LOCAL_REPOSITORY;
    }

    public static String getLocalBranch()
    {
        try
        {
            return getLocalRepository().getBranch();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Current branch could not be read", e);
        }
    }

    private static Repository readLocalRepository()
    {
        try
        {
            Repository repo  = new FileRepositoryBuilder()
                                    .readEnvironment() // scan environment GIT_* variables
                                    .findGitDir() // scan up the file system tree
                                    .build();
            return repo;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Repository could not be read", e);
        }
    }
}
