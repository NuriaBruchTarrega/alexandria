package nl.uva.alexandria.controller;

import nl.uva.alexandria.logic.Analyzer;
import nl.uva.alexandria.logic.experiments.AnalysisComparator;
import nl.uva.alexandria.logic.experiments.BenchmarkRunner;
import nl.uva.alexandria.logic.experiments.SensitivityAnalysis;
import nl.uva.alexandria.logic.experiments.ValidateExperiment;
import nl.uva.alexandria.model.dto.request.AnalysisRequest;
import nl.uva.alexandria.model.dto.request.FileRequest;
import nl.uva.alexandria.model.dto.response.AnalysisResponse;
import nl.uva.alexandria.model.dto.response.ComparisonResponse;
import nl.uva.alexandria.model.dto.response.ValidationResponse;
import nl.uva.alexandria.model.experiments.AnalysisSummary;
import nl.uva.alexandria.model.experiments.Difference;
import nl.uva.alexandria.model.experiments.SensitivityAnalysisData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class AnalysisController {

    private static final Logger LOG = LoggerFactory.getLogger(AnalysisController.class);

    private final Analyzer analyzer;
    private final AnalysisComparator analysisComparator;

    public AnalysisController(Analyzer analyzer, AnalysisComparator analysisComparator) {
        this.analyzer = analyzer;
        this.analysisComparator = analysisComparator;
    }

    @CrossOrigin
    @PostMapping("/analyze")
    public AnalysisResponse analyze(@RequestBody AnalysisRequest request) {
        return analyzer.analyze(request.getGroupID(), request.getArtifactID(), request.getVersion());
    }

    @CrossOrigin
    @PostMapping("/comparison")
    public ComparisonResponse comparison(@RequestBody FileRequest request) {
        Set<Difference> differences = analysisComparator.compare(request.getPathToFile());
        return new ComparisonResponse(differences);
    }

    @CrossOrigin
    @PostMapping("/validation")
    public ValidationResponse validation(@RequestBody FileRequest request) {
        Set<AnalysisSummary> result = ValidateExperiment.run(request.getPathToFile());
        ValidationResponse response = ValidationResponse.from(result);
        LOG.info("Validation finished, result: \n{}", response);
        return response;
    }

    @CrossOrigin
    @PostMapping("/sensitivity")
    public Set<SensitivityAnalysisData> sensitivity(@RequestBody FileRequest request) {
        Set<SensitivityAnalysisData> result = SensitivityAnalysis.run(request.getPathToFile());
        LOG.info("Sensitivity analysis finished");
        return result;
    }

    @CrossOrigin
    @PostMapping("/benchmark")
    public void benchmark(@RequestBody FileRequest request) {
        BenchmarkRunner.run(request.getPathToFile());
        LOG.info("Benchmark finished");
    }
}
