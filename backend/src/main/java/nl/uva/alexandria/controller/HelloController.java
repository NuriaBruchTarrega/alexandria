package nl.uva.alexandria.controller;

import nl.uva.alexandria.logic.Analyzer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final Analyzer analyzer;

    public HelloController(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name) {
        return analyzer.greeting(name);
    }
}
