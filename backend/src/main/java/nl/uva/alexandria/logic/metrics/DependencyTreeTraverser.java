package nl.uva.alexandria.logic.metrics;

import nl.uva.alexandria.model.DependencyTreeNode;

import java.util.ArrayDeque;
import java.util.Queue;

public class DependencyTreeTraverser {
    public void traverseTree(DependencyTreeNode rootNode) {
        traverseClientLibrary(rootNode);
        iterateTree(rootNode);
    }

    private void traverseClientLibrary(DependencyTreeNode rootNode) {
        // 1. Find fields from other libraries - I need a map only for this type of connection
        // 2. Find params from other libraries
        // 3. Find return types from other libraries
        // 4. Find superclasses in other libraries
        // 5. Find method calls to other libraries - I need a map only for this type of connection
        // 6. Find annotations from other libraries - I need a map for only this type of connection
    }

    private void iterateTree(DependencyTreeNode rootNode) {
        Queue<DependencyTreeNode> toVisit = new ArrayDeque<>(rootNode.getChildren());

        while (!toVisit.isEmpty()) {
            DependencyTreeNode visiting = toVisit.poll();
            // 1. Check for reachability at all
            // 2. Find inheritance of the reachable stuff
            traverseServerLibrary();
            toVisit.addAll(visiting.getChildren());
        }
    }

    private void traverseServerLibrary() {
        // 1. Reachable API methods
        // Follow the trace through method calls - special map
        // Find params and return types and either add to toVisit or add to reachable of other libraries
        // Same with annotations - But add also to the special map for annotations
        // 2. Reachable API classes:
        // Follow the trace through field declaration - special map
        // Find superclasses and either add to toVisit or add to reachable of other libraries
        // Same with annotations - But add also to the special map for annotations
    }
}
