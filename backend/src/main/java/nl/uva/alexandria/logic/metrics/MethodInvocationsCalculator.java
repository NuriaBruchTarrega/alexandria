package nl.uva.alexandria.logic.metrics;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ServerMethod;
import nl.uva.alexandria.model.factories.ServerMethodFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MethodInvocationsCalculator {

    private final Map<Library, Integer> mapMIC;
    private Map<ServerMethod, Integer> stableInvokedMethods;
    private final ClassPoolManager classPoolManager;

    public MethodInvocationsCalculator(Map<Library, Integer> mapMIC, ClassPoolManager classPoolManager) {
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
                                CtMethod serverMethod = mc.getMethod();
                                CtClass serverClass = serverMethod.getDeclaringClass();

                                // Filter out everything that is not in the server libraries
                                if (classPoolManager.isClassInServerLibrary(serverClass)) {
                                    ServerMethod sm = ServerMethodFactory.getServerMethodFromMethodAndClass(serverMethod, serverClass, serverClass.getURL().getPath());
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

    private void updateMapMIC() {
        stableInvokedMethods.forEach((serverMethod, numCalls) -> {
            Library library = serverMethod.getLibrary();
            mapMIC.computeIfPresent(library, (key, value) -> value + numCalls);
            mapMIC.putIfAbsent(library, numCalls);
        });
    }
}
