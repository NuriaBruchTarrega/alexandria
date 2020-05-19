package nl.uva.alexandria.model.factories;

import javassist.CtClass;
import javassist.CtMethod;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ServerMethod;

public class ServerMethodFactory {

    public static ServerMethod getServerMethodFromMethodAndClass(CtMethod method, CtClass declaringClass, String path) {
        Library library = LibraryFactory.getLibraryFromClassPath(path);
        return new ServerMethod(library, declaringClass, method);
    }
}
