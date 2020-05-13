package nl.uva.alexandria.model;

public class ServerMethod {
    private String library;
    private String clazz;
    private String method;

    public ServerMethod(String library, String clazz, String method) {
        this.library = library;
        this.clazz = clazz;
        this.method = method;
    }

    public String getLibrary() {
        return library;
    }

    public String getClassName() {
        return clazz;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServerMethod) {
            return this.library.equals(((ServerMethod) obj).library) && this.clazz.equals(((ServerMethod) obj).clazz) && this.method.equals(((ServerMethod) obj).method);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.library.hashCode() + this.clazz.hashCode() + this.method.hashCode();
    }
}
