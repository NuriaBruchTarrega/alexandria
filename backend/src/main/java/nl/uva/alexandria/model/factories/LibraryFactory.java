package nl.uva.alexandria.model.factories;

import nl.uva.alexandria.logic.utils.ClassNameUtils;
import nl.uva.alexandria.model.Library;

public class LibraryFactory {

    private LibraryFactory() {
    }

    public static Library getLibraryFromClassPath(String classPath) {
        String pathToJar = ClassNameUtils.getJarPathFromClassPath(classPath);
        return ClassNameUtils.getLibraryFromJarPath(pathToJar);
    }

    public static Library getLibraryFromGAV(String gav) {
        String[] split = gav.split(":");
        return new Library(split[0], split[1], split[2]);
    }
}
