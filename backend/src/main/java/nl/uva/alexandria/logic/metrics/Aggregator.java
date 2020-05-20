package nl.uva.alexandria.logic.metrics;

import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ServerClass;

import java.util.Map;

public class Aggregator {

    public static Map<Library, Integer> joinByLibrary(Map<? extends ServerClass, Integer> mapToJoin, Map<Library, Integer> joinedByLibrary) {
        mapToJoin.forEach((serverClass, num) -> {
            Library library = serverClass.getLibrary();
            joinedByLibrary.computeIfPresent(library, (key, value) -> value + num);
            joinedByLibrary.putIfAbsent(library, num);
        });

        return joinedByLibrary;
    }
}
