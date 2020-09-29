import {Injectable} from '@angular/core';
import {environment} from '@src/environments/environment';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {buildSensitivityAnalysis} from '@builders/sensitivity.builder';

const apiBaseUrl = environment.apiBaseUrl;

@Injectable({
  providedIn: 'root'
})
export class ExperimentsService {

  constructor(private httpClient: HttpClient) {
  }

  sensitivityAnalysis(pathToFile: string) {
    const result = this.httpClient.post(`${apiBaseUrl}/sensitivity`, {pathToFile})
      .pipe(map(res => buildSensitivityAnalysis(res)));
    return result;
  }
}
