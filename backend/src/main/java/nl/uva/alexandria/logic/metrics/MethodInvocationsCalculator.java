package nl.uva.alexandria.logic.metrics;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.utils.ClassNameUtils;
import nl.uva.alexandria.model.ServerMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MethodInvocationsCalculator {

    private final Map<String, Integer> mapMIC;
    private final ClassPoolManager classPoolManager;

    public MethodInvocationsCalculator(Map<String, Integer> mapMIC, ClassPoolManager classPoolManager) {
        this.mapMIC = mapMIC;
        this.classPoolManager = classPoolManager;
    }

    public void calculateMethodInvocations(Set<CtClass> clientClasses) {
        // Get calls by method
        Map<ServerMethod, Integer> methodCalls = getCallsByMethod(clientClasses);

        // Get polymorphic methods

        // Join by library
        updateMapMIC(methodCalls);

        // return mic;
    }

    private Map<ServerMethod, Integer> getCallsByMethod(Set<CtClass> clientClasses) {
        Map<ServerMethod, Integer> methodCalls = new HashMap<>();

        clientClasses.forEach(clientClass -> {
            CtMethod[] methods = clientClass.getDeclaredMethods();

            for (CtMethod method : methods) {
                try {
                    method.instrument(new ExprEditor() {
                        public void edit(MethodCall mc) {
                            try {
                                CtClass serverClass = mc.getMethod().getDeclaringClass();

                                // Filter out everything that is not in the server libraries
                                if (classPoolManager.isClassInServerLibrary(serverClass)) {
                                    ServerMethod sm = createServerMethod(mc);
                                    methodCalls.computeIfPresent(sm, (key, value) -> value + 1);
                                    methodCalls.putIfAbsent(sm, 1);
                                }
                            } catch (NotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            }
        });

        return methodCalls;
    }

    private ServerMethod createServerMethod(MethodCall mc) throws NotFoundException {
        CtClass serverClass = mc.getMethod().getDeclaringClass();

        String method = mc.getMethodName();
        String library = ClassNameUtils.getLibraryName(serverClass.getURL().getFile());
        String className = serverClass.getName();

        return new ServerMethod(library, className, method);
    }

    private void updateMapMIC(Map<ServerMethod, Integer> methodCalls) {
        methodCalls.forEach((serverMethod, numCalls) -> {
            String library = serverMethod.getLibrary();
            mapMIC.computeIfPresent(library, (key, value) -> value + numCalls);
            mapMIC.putIfAbsent(library, numCalls);
        });
    }
}
