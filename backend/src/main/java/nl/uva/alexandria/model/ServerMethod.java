package nl.uva.alexandria.model;

public class ServerMethod {
    private String library;
    private String className;
    private String method;

    public ServerMethod(String library, String className, String method) {
        this.library = library;
        this.className = className;
        this.method = method;
    }

    public String getLibrary() {
        return library;
    }

    public String getClassName() {
        return className;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServerMethod) {
            return this.library.equals(((ServerMethod) obj).library) && this.className.equals(((ServerMethod) obj).className) && this.method.equals(((ServerMethod) obj).method);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.library.hashCode() + this.className.hashCode() + this.method.hashCode();
    }
}
