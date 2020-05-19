package nl.uva.alexandria.model.factories;

import javassist.CtClass;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ServerClass;

public class ServerClassFactory {

    public static ServerClass getServerClassFromCtClass(CtClass ctClass, String path) {
        Library library = LibraryFactory.getLibraryFromClassPath(path);
        return new ServerClass(library, ctClass);
    }
}
