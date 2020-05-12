package nl.uva.alexandria.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Analyzer {

    private static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);

    private final Parser parser;

    public Analyzer(Parser parser) {
        this.parser = parser;
    }

    public void analyze(String path, String clientLibrary) {
        LOG.info("Analyze");
    }
}
