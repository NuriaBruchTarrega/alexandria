package nl.uva.alexandria.model.sattose;

import java.util.List;

public class DependencySpecification {
    private String product;
    private String forge;
    private List<String> constraints;

    public DependencySpecification(String product, String forge, List<String> constraints) {
        this.product = product;
        this.forge = forge;
        this.constraints = constraints;
    }

    public String getProduct() {
        return product;
    }

    public String getForge() {
        return forge;
    }

    public List<String> getConstraints() {
        return constraints;
    }
}
