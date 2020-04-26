package nl.uva.alexandria.controller;

import nl.uva.alexandria.logic.Analyzer;
import nl.uva.alexandria.model.dto.request.GreetingRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final Analyzer analyzer;

    public HelloController(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @PostMapping("/greeting")
    public String greeting(@RequestBody GreetingRequest request) {
        return analyzer.greeting(request.getName());
    }
}
