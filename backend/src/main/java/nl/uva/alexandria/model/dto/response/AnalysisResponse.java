package nl.uva.alexandria.model.dto.response;

import java.util.Map;

public class AnalysisResponse {
    private Map<String, Integer> MIC;
    private Map<String, Integer> AC;

    public AnalysisResponse(Map<String, Integer> MIC, Map<String, Integer> AC) {
        this.MIC = MIC;
        this.AC = AC;
    }

    public void setMIC(Map<String, Integer> MIC) {
        this.MIC = MIC;
    }

    public void setAC(Map<String, Integer> AC) {
        this.AC = AC;
    }

    public Map<String, Integer> getMIC() {
        return MIC;
    }

    public Map<String, Integer> getAC() {
        return AC;
    }
}
