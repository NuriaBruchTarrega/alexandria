package nl.uva.alexandria.logic.metrics;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import nl.uva.alexandria.model.ServerMethod;
import nl.uva.alexandria.utils.ClassNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MethodInvocationsCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(MethodInvocationsCalculator.class);

    public Map<String, Integer> calculateMethodInvocations(Set<CtClass> clientClasses) {
        // Get calls by method
        Map<ServerMethod, Integer> methodCalls = getCallsByMethod(clientClasses);

        // Get polymorphic methods

        // Join by library
        var micByLibrary = getMICByLibrary(methodCalls);

        return micByLibrary;
    }

    private Map<ServerMethod, Integer> getCallsByMethod(Set<CtClass> clientClasses) {
        Map<ServerMethod, Integer> methodCalls = new HashMap<>();

        clientClasses.forEach(clientClass -> {
            CtMethod[] methods = clientClass.getDeclaredMethods();

            for (CtMethod method : methods) {
                try {
                    method.instrument(
                            new ExprEditor() {
                                public void edit(MethodCall mc) {
                                    try {
                                        CtClass serverClass = mc.getMethod().getDeclaringClass();
                                        URL url = serverClass.getURL();

                                        // Filter out everything that is not in the server libraries
                                        if (url.getProtocol().equals("jar") && url.getPath().contains("target/dependency")) {
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

    private Map<String, Integer> getMICByLibrary(Map<ServerMethod, Integer> methodCalls) {
        Map<String, Integer> micByLibrary = new HashMap<>();

        methodCalls.forEach((serverMethod, numCalls) -> {
            String library = serverMethod.getLibrary();
            micByLibrary.computeIfPresent(library, (key, value) -> value + numCalls);
            micByLibrary.putIfAbsent(library, numCalls);
        });

        return micByLibrary;
    }
}