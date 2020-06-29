package nl.uva.alexandria.controller;

import nl.uva.alexandria.logic.Analyzer;
import nl.uva.alexandria.model.dto.request.AnalysisRequest;
import nl.uva.alexandria.model.dto.request.SattoseData;
import nl.uva.alexandria.model.dto.response.AnalysisResponse;
import nl.uva.alexandria.sattose.CallGraphAnalyzer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisController {

    private final Analyzer analyzer;
    private final CallGraphAnalyzer callGraphAnalyzer;

    public AnalysisController(Analyzer analyzer, CallGraphAnalyzer callGraphAnalyzer) {
        this.analyzer = analyzer;
        this.callGraphAnalyzer = callGraphAnalyzer;
    }

    @PostMapping("/analyze")
    public AnalysisResponse analyze(@RequestBody AnalysisRequest request) {
        return analyzer.analyze(request.getGroupID(), request.getArtifactID(), request.getVersion());
    }

    @PostMapping("/sattose")
    public void sattose(@RequestBody SattoseData request) {
        callGraphAnalyzer.analyze(request.getPathToFolder());
    }
}
