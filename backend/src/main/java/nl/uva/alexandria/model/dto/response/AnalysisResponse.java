package nl.uva.alexandria.model.dto.response;

import nl.uva.alexandria.model.DependencyTreeResult;
import nl.uva.alexandria.model.Library;

import java.util.Map;

public class AnalysisResponse {
    private DependencyTreeResult MIC;
    private Map<Library, Integer> AC;

    public AnalysisResponse(DependencyTreeResult MIC, Map<Library, Integer> AC) {
        this.MIC = MIC;
        this.AC = AC;
    }

    public DependencyTreeResult getMIC() {
        return MIC;
    }

    public void setMIC(DependencyTreeResult MIC) {
        this.MIC = MIC;
    }

    public Map<Library, Integer> getAC() {
        return AC;
    }

    public void setAC(Map<Library, Integer> AC) {
        this.AC = AC;
    }
}
