import {Component, OnInit} from '@angular/core';
import {AnalyzerService} from '../../services/analyzer.service';
import {Library} from '../../models/library';

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

  doAnalyzeRequest(library: Library) {
    this.analyzerService
      .analyzeLibrary(library)
      .subscribe(res => {
        console.log(res);
      });
  }

}
