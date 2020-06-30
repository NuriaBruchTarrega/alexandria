package nl.uva.alexandria.model.sattose;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class LibraryCallGraphFactory {

    public static LibraryCallGraph getLibraryCallGraphFromJson(JSONObject libraryJson) {
        String product = (String) libraryJson.get("product");
        String forge = (String) libraryJson.get("forge");
        String generator = (String) libraryJson.get("generator");
        List<DependencySpecification> depSet = getDepSetFromJson((JSONArray) libraryJson.get("depset"));
        String version = (String) libraryJson.get("version");
        Cha cha = getChaFromJson((JSONObject) libraryJson.get("cha"));
        Graph graph = getGraphFromJson((JSONObject) libraryJson.get("graph"));
        Long timestamp = (Long) libraryJson.get("timestamp");

        return new LibraryCallGraph(product, forge, generator, depSet, version, cha, graph, timestamp);
    }

    private static List<DependencySpecification> getDepSetFromJson(JSONArray depSet) {
        List<DependencySpecification> parsedDependencies = new ArrayList<>();

        for (Object dep : depSet) {
            JSONArray dependency = (JSONArray) dep;
            parsedDependencies.add(getDependencyFromJson((JSONObject) dependency.get(0)));
        }
        return parsedDependencies;
    }

    private static DependencySpecification getDependencyFromJson(JSONObject dependency) {
        String product = (String) dependency.get("product");
        String forge = (String) dependency.get("product");
        JSONArray constraintsJSON = (JSONArray) dependency.get("constraints");

        List<String> constraints = new ArrayList<>();
        constraintsJSON.forEach(constraint -> {
            constraints.add((String) constraint);
        });

        return new DependencySpecification(product, forge, constraints);
    }

    private static Cha getChaFromJson(JSONObject cha) {
        Map<String, ClassData> chaData = new HashMap<>();

        Set<Map.Entry> entrySet = cha.entrySet();
        entrySet.forEach(entry -> chaData.put((String) entry.getKey(), getClassDataFromJSON((JSONObject) entry.getValue())));

        return new Cha(chaData);
    }

    private static ClassData getClassDataFromJSON(JSONObject value) {
        Map<Long, String> methods = new HashMap<>();
        Set<Map.Entry> methodsEntries = ((JSONObject) value.get("methods")).entrySet();
        methodsEntries.forEach(entry -> methods.put(Long.parseLong((String) entry.getKey()), (String) entry.getValue()));

        List<String> superInterfaces = new ArrayList<>();
        ((JSONArray) value.get("superInterfaces")).forEach(interfaceJSON -> superInterfaces.add((String) interfaceJSON));

        List<String> superClasses = new ArrayList<>();
        ((JSONArray) value.get("superClasses")).forEach(superClass -> superClasses.add((String) superClass));

        return new ClassData(methods, superInterfaces, (String) value.get("sourceFile"), superClasses);
    }

    private static Graph getGraphFromJson(JSONObject graph) {
        List<InternalCall> internalCalls = new ArrayList<>();
        ((JSONArray) graph.get("internalCalls")).forEach(internalCall -> {
            JSONArray internalCallArray = (JSONArray) internalCall;
            internalCalls.add(new InternalCall((Long) internalCallArray.get(0), (Long) internalCallArray.get(1)));
        });

        List<ExternalCall> externalCalls = new ArrayList<>();
        ((JSONArray) graph.get("externalCalls")).forEach(externalCall -> {
            externalCalls.add(getExternalCallFromJson((JSONArray) externalCall));
        });

        return new Graph(internalCalls, externalCalls);
    }

    private static ExternalCall getExternalCallFromJson(JSONArray externalCall) {
        Long sourceMethod = Long.parseLong((String) externalCall.get(0));
        String targetMethod = (String) externalCall.get(1);

        Map<String, String> metadata = new HashMap<>();
        JSONObject metadataJson = (JSONObject) externalCall.get(2);
        Set<Map.Entry> metadataEntries = metadataJson.entrySet();
        metadataEntries.forEach(entry -> metadata.put((String) entry.getKey(), (String) entry.getValue()));

        return new ExternalCall(sourceMethod, targetMethod, metadata);
    }
}
