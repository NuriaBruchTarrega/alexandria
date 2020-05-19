package nl.uva.alexandria.logic.metrics;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.Cast;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.ServerMethod;

import java.util.ArrayList;
import java.util.List;

public class PolymorphismDetection {

    public int numPolymorphicMethods(ServerMethod sm, ClassPoolManager cpm) throws NotFoundException {
        String libraryJarPath = "holaquetal";
        CtClass[] libraryClasses = cpm.getLibraryClasses(libraryJarPath);
        List<CtMethod> polymorphicMethods = findPolymorphicMethods(sm, libraryClasses);

        return polymorphicMethods.size();
    }

    private List<CtMethod> findPolymorphicMethods(ServerMethod sm, CtClass[] libraryClasses) {
        List<CtMethod> polymorphicMethods = new ArrayList<>();

        CtClass serverClass = libraryClasses[0];
        CtMethod serverMethod = serverClass.getMethods()[0];

        for (CtClass libraryClass : libraryClasses) {
            if (!libraryClass.subclassOf(serverClass)) continue;
            try {
                CtMethod polymorphicMethod = libraryClass.getDeclaredMethod(serverMethod.getName(), serverMethod.getParameterTypes());
                polymorphicMethods.add(polymorphicMethod);
            } catch (NotFoundException e) {
                continue;
            }
        }

        return polymorphicMethods;
    }

    private class PolymorphismExprEditor extends ExprEditor {

        @Override
        public void edit(MethodCall m) throws CannotCompileException {
        }

        @Override
        public void edit(FieldAccess f) throws CannotCompileException {
        }

        @Override
        public void edit(Cast c) throws CannotCompileException {
        }
    }
}
