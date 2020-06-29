package nl.uva.alexandria.model.sattose;

import java.util.Map;

public class Cha {
    private Map<String, ClassData> productClasses;

    public Cha(Map<String, ClassData> productClasses) {
        this.productClasses = productClasses;
    }

    public Map<String, ClassData> getProductClasses() {
        return productClasses;
    }
}
