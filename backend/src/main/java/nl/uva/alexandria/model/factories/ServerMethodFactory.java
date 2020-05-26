package nl.uva.alexandria.model.factories;

import javassist.CtBehavior;
import javassist.CtClass;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ServerMethod;

public class ServerMethodFactory {

    public static ServerMethod getServerBehaviorAndClass(CtBehavior behavior, CtClass declaringClass, String path) {
        Library library = LibraryFactory.getLibraryFromClassPath(path);
        return new ServerMethod(library, declaringClass, behavior);
    }
}
