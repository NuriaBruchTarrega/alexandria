package nl.uva.alexandria.logic.metrics;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import nl.uva.alexandria.model.ServerMethod;
import nl.uva.alexandria.utils.ClassNameUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MethodInvocationsCalculator {
    private final Map<String, Integer> mic;

    public MethodInvocationsCalculator(Map<String, Integer> mic) {
        this.mic = mic;
    }

    public void calculateMethodInvocations(Set<CtClass> clientClasses) {
        // Get calls by method
        Map<ServerMethod, Integer> methodCalls = getCallsByMethod(clientClasses);

        // Get polymorphic methods

        // Join by library
        updateMIC(methodCalls);

        // return mic;
    }

    private Map<ServerMethod, Integer> getCallsByMethod(Set<CtClass> clientClasses) {
        Map<ServerMethod, Integer> methodCalls = new HashMap<>();

        clientClasses.forEach(clientClass -> {
            CtMethod[] methods = clientClass.getDeclaredMethods();

            for (CtMethod method : methods) {
                try {
                    ServerMethod sm = getServerMethod(method);
                    if (sm != null) {
                        methodCalls.computeIfPresent(sm, (key, value) -> value + 1);
                        methodCalls.putIfAbsent(sm, 1);
                    }
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            }
        });

        return methodCalls;
    }

    private ServerMethod getServerMethod(CtMethod method) throws CannotCompileException {
        final ServerMethod[] sm = {null};
        method.instrument(new ExprEditor() {
            public void edit(MethodCall mc) {
                try {
                    CtClass serverClass = mc.getMethod().getDeclaringClass();
                    URL url = serverClass.getURL();

                    // Filter out everything that is not in the server libraries
                    if (url.getProtocol().equals("jar") && url.getPath().contains("target/dependency")) {
                        sm[0] = createServerMethod(mc);
                    }
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        return sm[0];
    }


    private ServerMethod createServerMethod(MethodCall mc) throws NotFoundException {
        CtClass serverClass = mc.getMethod().getDeclaringClass();

        String method = mc.getMethodName();
        String library = ClassNameUtils.getLibraryName(serverClass.getURL().getFile());
        String className = serverClass.getName();

        return new ServerMethod(library, className, method);
    }

    private void updateMIC(Map<ServerMethod, Integer> methodCalls) {
        methodCalls.forEach((serverMethod, numCalls) -> {
            String library = serverMethod.getLibrary();
            mic.computeIfPresent(library, (key, value) -> value + numCalls);
            mic.putIfAbsent(library, numCalls);
        });
    }
}
