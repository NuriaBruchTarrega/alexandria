package nl.uva.alexandria.model;

import javassist.CtClass;

public class ServerClass {

    private final Library library;
    private final CtClass ctClass;

    public ServerClass(Library library, CtClass ctClass) {
        this.library = library;
        this.ctClass = ctClass;
    }

    public Library getLibrary() {
        return library;
    }

    public CtClass getCtClass() {
        return ctClass;
    }

    @Override
    public int hashCode() {
        return this.library.hashCode() + this.ctClass.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServerClass) {
            return this.library.equals(((ServerClass) obj).library) && this.ctClass.equals(((ServerClass) obj).ctClass);
        }
        return false;
    }
}
