package nl.uva.alexandria.logic.metrics;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;
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
    private Map<ServerMethod, Integer> stableInvokedMethods;
    private final ClassPoolManager classPoolManager;

    public MethodInvocationsCalculator(Map<String, Integer> mapMIC, ClassPoolManager classPoolManager) {
        this.mapMIC = mapMIC;
        this.classPoolManager = classPoolManager;
        this.stableInvokedMethods = new HashMap<>();
    }

    public void calculateMethodInvocations(Set<CtClass> clientClasses) {
        // Get calls by method
        getCallsByMethod(clientClasses);

        // Get polymorphic methods

        // Join by library
        updateMapMIC();

        // return mic;
    }

    private void getCallsByMethod(Set<CtClass> clientClasses) {
        clientClasses.forEach(clientClass -> {
            CtBehavior[] methods = clientClass.getDeclaredBehaviors();

            for (CtBehavior method : methods) {
                try {
                    // TODO: Do something about the volatile stuff
                    method.instrument(new ExprEditor() {
                        public void edit(MethodCall mc) {
                            try {
                                CtClass serverClass = mc.getMethod().getDeclaringClass();

                                // Filter out everything that is not in the server libraries
                                if (classPoolManager.isClassInServerLibrary(serverClass)) {
                                    ServerMethod sm = createServerMethod(mc);
                                    stableInvokedMethods.computeIfPresent(sm, (key, value) -> value + 1);
                                    stableInvokedMethods.putIfAbsent(sm, 1);
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
    }

    private ServerMethod createServerMethod(MethodCall mc) throws NotFoundException {
        CtClass serverClass = mc.getMethod().getDeclaringClass();

        String method = mc.getMethodName();
        String library = ClassNameUtils.getLibraryName(serverClass.getURL().getFile());
        String className = serverClass.getName();

        return new ServerMethod(library, className, method);
    }

    private void updateMapMIC() {
        stableInvokedMethods.forEach((serverMethod, numCalls) -> {
            String library = serverMethod.getLibrary();
            mapMIC.computeIfPresent(library, (key, value) -> value + numCalls);
            mapMIC.putIfAbsent(library, numCalls);
        });
    }
}
