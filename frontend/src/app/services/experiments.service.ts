import {Injectable} from '@angular/core';
import {environment} from '@src/environments/environment';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {buildBenchmarkResult, buildSensitivityAnalysis} from '@builders/experiments.builder';
import {Observable} from 'rxjs';
import {SensitivityResult} from '@models/experiments/sensitivity-result';
import {BenchmarkResult} from '@models/experiments/benchmark-result';

const apiBaseUrl = environment.apiBaseUrl;

@Injectable({
  providedIn: 'root'
})
export class ExperimentsService {

  constructor(private httpClient: HttpClient) {
  }

  sensitivityAnalysis(pathToFile: string): Observable<Set<SensitivityResult>> {
    const result = this.httpClient.post(`${apiBaseUrl}/sensitivity`, {pathToFile})
      .pipe(map(res => buildSensitivityAnalysis(res)));
    return result;
  }

  benchmarkRequest(pathToFile: string): Observable<BenchmarkResult> {
    const result = this.httpClient.post(`${apiBaseUrl}/benchmark`, {pathToFile})
      .pipe(map(res => buildBenchmarkResult(res)));
    return result;
  }
}
