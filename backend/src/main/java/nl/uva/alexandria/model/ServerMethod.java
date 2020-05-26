package nl.uva.alexandria.model;

import javassist.CtBehavior;
import javassist.CtClass;

public class ServerMethod extends ServerClass {

    private final CtBehavior behavior;

    public ServerMethod(Library library, CtClass declaringClass, CtBehavior behavior) {
        super(library, declaringClass);
        this.behavior = behavior;
    }

    public CtBehavior getBehavior() {
        return behavior;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServerMethod) {
            ServerClass serverClass = (ServerClass) obj;
            ServerMethod serverMethod = (ServerMethod) obj;
            return super.equals(serverClass) && this.behavior.equals(serverMethod.behavior);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.behavior.hashCode();
    }
}
