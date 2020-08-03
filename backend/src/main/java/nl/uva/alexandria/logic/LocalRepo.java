package nl.uva.alexandria.logic;

import java.io.File;

public class LocalRepo {
    // This class is going to be modified in case at some point it is decided to add Gradle or some other manager.
    public static final String LOCAL_REPO_BASE_PATH = String.join(File.separator, System.getProperty("user.home"), ".m2", "repository");
    public static final File localRepoFile = new File(LOCAL_REPO_BASE_PATH);
}
