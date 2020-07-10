import {Component, OnInit} from '@angular/core';
import {AnalyzerService} from '../../services/analyzer.service';

@Component({
  selector: 'analyzer',
  templateUrl: './analyzer.component.html',
  styleUrls: ['./analyzer.component.css']
})
export class AnalyzerComponent implements OnInit {

  constructor(private analyzerService: AnalyzerService) {
  }

  ngOnInit(): void {
  }

  doAnalyzeRequest({groupID, artifactID, versionID}) {
    this.analyzerService
      .analyzeLibrary(groupID, artifactID, versionID)
      .subscribe(res => {
        console.log(res);
      });
  }

}
