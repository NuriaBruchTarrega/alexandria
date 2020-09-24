import {SensitivityResult, SensitivityResultFactory} from '@models/experiments/sensitivity-result';

export function buildSensitivityAnalysis(res): Set<SensitivityResult> {
  const sensitivityResultSet: Set<SensitivityResult> = new Set<SensitivityResult>();

  res.forEach(sensitivityAnalysisData => {
    const sensitivityResult: SensitivityResult = SensitivityResultFactory.create(sensitivityAnalysisData);
    sensitivityResultSet.add(sensitivityResult);
  });

  return sensitivityResultSet;
}
