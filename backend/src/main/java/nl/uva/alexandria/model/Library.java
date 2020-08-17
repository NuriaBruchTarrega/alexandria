package nl.uva.alexandria.model;

import nl.uva.alexandria.logic.utils.ClassNameUtils;

import java.io.File;

import static nl.uva.alexandria.logic.LocalRepo.LOCAL_REPO_BASE_PATH;

public class Library {

    private static final String JAR = ".jar";

    private final String groupID;
    private final String artifactID;
    private final String version;

    private Library(String groupID, String artifactID, String version) {
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.version = version;
    }

    public static Library fromClassPath(String classPath) {
        String pathToJar = ClassNameUtils.getJarPathFromClassPath(classPath);
        return fromJarPath(pathToJar);
    }

    public static Library fromGAV(String gav) {
        String[] split = gav.split(":");
        return new Library(split[0], split[1], split[2]);
    }

    public static Library fromJarPath(String jarPath) {
        jarPath = jarPath.replace("/", File.separator);
        int indexRepository = jarPath.lastIndexOf(LOCAL_REPO_BASE_PATH);
        String substr = jarPath.substring(indexRepository + LOCAL_REPO_BASE_PATH.length() + 1, jarPath.length() - JAR.length());

        int indexSeparator = substr.lastIndexOf(File.separator);
        substr = substr.substring(0, indexSeparator);

        indexSeparator = substr.lastIndexOf(File.separator);
        String version = substr.substring(indexSeparator + 1);
        substr = substr.substring(0, indexSeparator);

        indexSeparator = substr.lastIndexOf(File.separator);
        String artifact = substr.substring(indexSeparator + 1);
        String group = substr.substring(0, indexSeparator);
        group = group.replace(File.separator, ".");


        return new Library(group, artifact, version);
    }

    public String getGroupID() {
        return groupID;
    }

    public String getArtifactID() {
        return artifactID;
    }

    public String getVersion() {
        return version;
    }

    public String getLibraryPath() {
        String libraryFolderPath = String.join(File.separator, LOCAL_REPO_BASE_PATH, groupID.replace(".", File.separator), artifactID, version);
        return String.join(File.separator, libraryFolderPath, artifactID + "-" + version + ".jar");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Library) {
            Library library = (Library) obj;
            return this.groupID.equals(library.groupID)
                    && this.artifactID.equals(library.artifactID)
                    && this.version.equals(library.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return groupID.hashCode() + artifactID.hashCode() + version.hashCode();
    }

    @Override
    public String toString() {
        return groupID + ":" + artifactID + ":" + version;
    }
}
