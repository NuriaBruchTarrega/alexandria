package nl.uva.alexandria.logic.metrics;

import javassist.*;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.ServerMethod;
import nl.uva.alexandria.model.factories.ServerMethodFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static nl.uva.alexandria.logic.utils.GeneralUtils.stackTraceToString;

public class MethodInvocationsCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(MethodInvocationsCalculator.class);
    private final ClassPoolManager classPoolManager;
    private Map<ServerMethod, Integer> stableInvokedMethods = new HashMap<>();


    public MethodInvocationsCalculator(ClassPoolManager classPoolManager) {
        this.classPoolManager = classPoolManager;
    }

    public Map<ServerMethod, Integer> calculateMethodInvocations() {
        // Get calls by method
        Set<CtClass> clientClasses = classPoolManager.getClientClasses();
        getCallsByMethod(clientClasses);

        // Get polymorphic methods
        Map<ServerMethod, Integer> mapMicPolymorphism = new HashMap<>();
        stableInvokedMethods.forEach((serverMethod, numInvocations) -> {
            try {
                Integer numPolymorphicMethods = PolymorphismDetection.numPolymorphicMethods(serverMethod, classPoolManager);
                mapMicPolymorphism.put(serverMethod, numInvocations * numPolymorphicMethods);
            } catch (NotFoundException e) {
                LOG.error("Error obtaining polymorphic implementations\n\n{}", stackTraceToString(e));
            }
        });

        return mapMicPolymorphism;
    }

    private void getCallsByMethod(Set<CtClass> clientClasses) {
        clientClasses.forEach(clientClass -> {
            CtBehavior[] methods = clientClass.getDeclaredBehaviors();

            for (CtBehavior method : methods) {
                // getDeclaredBehaviors returns bridge methods as well, which are not needed to calculate the metric.
                // Bridge methods are marked as volatile
                if (Modifier.isVolatile(method.getModifiers())) continue;

                try {
                    method.instrument(new ExprEditor() {
                        public void edit(MethodCall methodCall) {
                            try {
                                computeBehavior(methodCall.getMethod());
                            } catch (NotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        public void edit(ConstructorCall constructorCall) {
                            try {
                                computeBehavior(constructorCall.getConstructor());
                            } catch (NotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (CannotCompileException e) {
                    LOG.warn("Error on method.instrument\n\n{}", stackTraceToString(e));
                }
            }
        });
    }

    private void computeBehavior(CtBehavior ctBehavior) {
        try {
            CtClass serverCtClass = ctBehavior.getDeclaringClass();

            // Filter out everything that is not in the server libraries
            if (classPoolManager.isClassInServerLibrary(serverCtClass)) {
                ServerMethod serverMethod = ServerMethodFactory.getServerBehaviorAndClass(ctBehavior, serverCtClass, serverCtClass.getURL().getPath());
                stableInvokedMethods.computeIfPresent(serverMethod, (key, value) -> value + 1);
                stableInvokedMethods.putIfAbsent(serverMethod, 1);
            }
        } catch (NotFoundException e) {
            LOG.warn("Class not found\n\n{}", stackTraceToString(e));

        }
    }
}
