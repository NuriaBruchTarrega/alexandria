package nl.uva.alexandria.model.dto.response;

import nl.uva.alexandria.model.Library;

import java.util.Map;

public class AnalysisResponse {
    private Map<Library, Integer> MIC;
    private Map<Library, Integer> AC;

    public AnalysisResponse(Map<Library, Integer> MIC, Map<Library, Integer> AC) {
        this.MIC = MIC;
        this.AC = AC;
    }

    public void setMIC(Map<Library, Integer> MIC) {
        this.MIC = MIC;
    }

    public void setAC(Map<Library, Integer> AC) {
        this.AC = AC;
    }

    public Map<Library, Integer> getMIC() {
        return MIC;
    }

    public Map<Library, Integer> getAC() {
        return AC;
    }
}
