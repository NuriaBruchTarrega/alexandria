package nl.uva.alexandria.model;

public class ServerClass {
    private String library;
    private String className;

    public ServerClass(String library, String className) {
        this.library = library;
        this.className = className;
    }

    public String getLibrary() {
        return library;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public int hashCode() {
        return this.library.hashCode() + this.className.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServerClass) {
            return this.library.equals(((ServerClass) obj).library) && this.className.equals(((ServerClass) obj).className);
        }
        return false;
    }
}