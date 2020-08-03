package nl.uva.alexandria.logic.metrics.inheritance;

import javassist.CtClass;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;

import java.util.Set;

public abstract class InheritanceDetector {

    protected final ClassPoolManager classPoolManager;
    protected Set<CtClass> currentLibraryClasses;
    protected Library currentLibrary = null;

    public InheritanceDetector(ClassPoolManager classPoolManager) {
        this.classPoolManager = classPoolManager;
    }

    public abstract void calculateInheritanceOfDependencyTreeNode(DependencyTreeNode dependencyTreeNode) throws NotFoundException;

    protected void updateCurrentLibrary(Library library) throws NotFoundException {
        if (!library.equals(currentLibrary)) {
            this.currentLibrary = library;
            this.currentLibraryClasses = classPoolManager.getLibraryClasses(library.getLibraryPath());
        }
    }

    protected boolean libraryClassImplementsOrExtendsReachableClass(CtClass libraryClass, CtClass reachableClass) throws NotFoundException {
        if (libraryClass.equals(reachableClass)) return false;
        if (libraryClass.subclassOf(reachableClass)) return true;
        if (reachableClass.isInterface()) {
            CtClass[] interfaces = libraryClass.getInterfaces();
            for (CtClass implementedInterface : interfaces) {
                if (implementedInterface.equals(reachableClass)) return true;
            }
        }
        return false;
    }
}
