package nl.uva.alexandria.model;

import javassist.CtClass;
import javassist.CtMethod;

public class ServerMethod extends ServerClass {

    private final CtMethod method;

    public ServerMethod(Library library, CtClass declaringClass, CtMethod method) {
        super(library, declaringClass);
        this.method = method;
    }

    public CtMethod getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServerMethod) {
            ServerClass serverClass = (ServerClass) obj;
            ServerMethod serverMethod = (ServerMethod) obj;
            return super.equals(serverClass) && this.method.equals(serverMethod.method);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.method.hashCode();
    }
}
