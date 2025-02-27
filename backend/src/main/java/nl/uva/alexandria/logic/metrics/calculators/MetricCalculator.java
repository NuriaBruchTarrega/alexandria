package nl.uva.alexandria.logic.metrics.calculators;

import javassist.CtClass;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.metrics.inheritance.InheritanceDetector;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class MetricCalculator {
    protected static final Logger LOG = LoggerFactory.getLogger(MetricCalculator.class);

    protected final ClassPoolManager classPoolManager;
    protected final InheritanceDetector inheritanceDetector;
    protected final DependencyTreeNode rootLibrary;

    public MetricCalculator(ClassPoolManager classPoolManager, InheritanceDetector inheritanceDetector, DependencyTreeNode rootLibrary) {
        this.classPoolManager = classPoolManager;
        this.inheritanceDetector = inheritanceDetector;
        this.rootLibrary = rootLibrary;
    }

    public abstract void visitClientLibrary();

    public abstract void visitServerLibrary(DependencyTreeNode currentLibrary);

    public abstract void findInheritanceOfServerLibrary(DependencyTreeNode visiting);

    protected CtClass getClassInArray(CtClass ctClass) throws NotFoundException {
        String className = ctClass.getName();

        while (className.endsWith("[]")) {
            className = className.substring(0, className.length() - 2);
        }

        return classPoolManager.getClassFromClassName(className);
    }

    protected void addReachableClass(CtClass ctClass, int distance) throws NotFoundException {
        Library serverLibrary = Library.fromClassPath(ctClass.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = this.rootLibrary.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) {
            libraryNode.get().addReachableClass(ctClass, distance);
        } else {
            LOG.warn("Library not found in tree: {}", serverLibrary);
        }
    }
}
