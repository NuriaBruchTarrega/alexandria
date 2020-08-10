import {NodeColor, NodeColorFactory} from './color';
import {MetricDistance} from './metric.distance';
import {ClassDistribution} from './class.distribution';

export class TreeNodeFactory {
  static create({
                  id = 0, groupID = '', artifactID = '', version = '',
                  title = '', level = 0, font = {multi: 'md'},
                  micDistance = null, acDistance = null, annotationsDistance = null,
                  micClassDistribution = null, acClassDistribution = null, bloated = false
                }): TreeNode {
    return new TreeNode(id, groupID, artifactID, version, title,
      level, font, micDistance, acDistance, annotationsDistance, micClassDistribution, acClassDistribution, bloated);
  }
}

export interface ITreeNode {
  id: number;
  label: string;
}

export class TreeNode implements ITreeNode {
  id: number;
  label: string; // Content of the node
  title: string; // Tooltip
  level: number;
  color: NodeColor;
  font: any;
  groupID: string;
  artifactID: string;
  version: string;
  micDistance: MetricDistance;
  acDistance: MetricDistance;
  annotationDistance: MetricDistance;
  micClassDistribution: ClassDistribution;
  acClassDistribution: ClassDistribution;
  tac: number;
  tann: number;
  tmic: number;
  bloated: boolean;

  constructor(id: number, groupID: string,
              artifactID: string, version: string,
              title: string, level: number, font: any,
              micDistance: MetricDistance, acDistance: MetricDistance, annotationDistance: MetricDistance,
              micClassDistribution: ClassDistribution, acClassDistribution: ClassDistribution, bloated: boolean) {
    this.id = id;
    this.groupID = groupID;
    this.artifactID = artifactID;
    this.version = version;
    this.title = title;
    this.level = level;
    this.font = font;
    this.micDistance = micDistance;
    this.acDistance = acDistance;
    this.annotationDistance = annotationDistance;
    this.micClassDistribution = micClassDistribution;
    this.acClassDistribution = acClassDistribution;
    this.tmic = 0;
    this.tac = 0;
    this.tann = 0;
    this.createLabel();
    this.calculateColor(bloated);
  }

  getLibraryCompleteName(): string {
    return `${this.groupID}.${this.artifactID}.${this.version}`;
  }

  calculateTmic(factor: number) {
    if (this.level === 1) {
      this.tmic = this.micDistance.getValueAtDistance(1) || 0;
    } else {
      this.tmic = this.micDistance.calculateMetric(factor);
    }
    this.createLabel();
  }

  calculateTac(factor: number) {
    if (this.level === 1) {
      this.tac = this.acDistance.getValueAtDistance(1) || 0;
    } else {
      this.tac = this.acDistance.calculateMetric(factor);
    }
    this.createLabel();
  }

  calculateTAnnotations(factor: number) {
    if (this.level === 1) {
      this.tac = this.acDistance.getValueAtDistance(1) || 0;
    } else {
      this.tac = this.acDistance.calculateMetric(factor);
    }
    this.createLabel();
  }

  createLabel() {
    this.label = `*Group Id:* ${this.groupID}\n*Artifact Id:* ${this.artifactID}\n*Version:* ${this.version}\n`;

    if (this.level > 1) {
      this.label += `*TMIC:* ${this.tmic} *TAC:* ${this.tac} *TA* ${this.tann}`;
    } else if (this.level === 1) {
      this.label += `*MIC:* ${this.tmic} *AC:* ${this.tac} *A* ${this.tann}`;
    }
  }

  calculateColor(bloated: boolean) {
    this.color = NodeColorFactory.create(this.level, bloated);
  }
}
