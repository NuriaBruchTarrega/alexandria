import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {buildDependencyGraph} from '../builders/analyzer.builder';
import {Library} from '../models/library';

const apiBaseUrl = environment.apiBaseUrl;

@Injectable()
export class AnalyzerService {

  constructor(private httpClient: HttpClient) {
  }

  analyzeLibrary(library: Library) {
    const {groupID, artifactID, version} = library;
    const result = this.httpClient.post(`${apiBaseUrl}/analyze`, {groupID, artifactID, version})
      .pipe(map(res => buildDependencyGraph(res)));
    return result;
  }
}
