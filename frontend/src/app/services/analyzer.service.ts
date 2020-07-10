import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';

const apiBaseUrl = environment.apiBaseUrl;

@Injectable({
  providedIn: 'root'
})
export class AnalyzerService {

  constructor(private httpClient: HttpClient) {
  }

  analyzeLibrary(groupID: string, artifactID: string, version: string) {
    const result = this.httpClient.post(`${apiBaseUrl}/analyze`, {groupID, artifactID, version});
  }

}
