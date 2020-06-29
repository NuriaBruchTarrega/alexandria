package nl.uva.alexandria.model.sattose;

import java.util.List;

public class LibraryCallGraph {
    private String product;
    private String forge;
    private String generator;
    private List<DependencySpecification> depSet;
    private String version;
    private Cha cha;
    private Graph graph;
    private Long timestamp;

    public LibraryCallGraph(String product, String forge, String generator, List<DependencySpecification> depSet, String version, Cha cha, Graph graph, Long timestamp) {
        this.product = product;
        this.forge = forge;
        this.generator = generator;
        this.depSet = depSet;
        this.version = version;
        this.cha = cha;
        this.graph = graph;
        this.timestamp = timestamp;
    }

    public String getProduct() {
        return product;
    }

    public String getForge() {
        return forge;
    }

    public String getGenerator() {
        return generator;
    }

    public List<DependencySpecification> getDepSet() {
        return depSet;
    }

    public String getVersion() {
        return version;
    }

    public Cha getCha() {
        return cha;
    }

    public Graph getGraph() {
        return graph;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
