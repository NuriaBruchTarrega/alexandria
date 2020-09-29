import {Injectable} from '@angular/core';
import {environment} from '@src/environments/environment';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {buildSensitivityAnalysis} from '@builders/sensitivity.builder';
import {Observable} from 'rxjs';
import {SensitivityResult} from '@models/experiments/sensitivity-result';

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

  benchmarkRequest(path: string): Observable<any> {
    // TODO: to request
    return new Observable<any>();
  }
}
