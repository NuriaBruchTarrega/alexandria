package nl.uva.alexandria.logic;

import org.springframework.stereotype.Component;

@Component
public class Analyzer {

    private final Parser parser;

    public Analyzer(Parser parser) {
        this.parser = parser;
    }

    public String greeting(String name) {
        return "Holi " + name;
    }
}
