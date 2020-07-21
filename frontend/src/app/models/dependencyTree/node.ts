import {NodeColor} from './color';
import {MetricDistance} from './metric.distance';

export class TreeNodeFactory {
  static create({
                  id = 0, groupID = '', artifactID = '', version = '',
                  title = '', level = 0, color = null, font = {multi: 'md'},
                  micDistance = null, acDistance = null
                }): TreeNode {
    return new TreeNode(id, groupID, artifactID, version, title, level, color, font, micDistance, acDistance);
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
  tac: number;
  tmic: number;

  constructor(id: number, groupID: string,
              artifactID: string, version: string,
              title: string, level: number,
              color: NodeColor, font: any,
              micDistance: MetricDistance, acDistance: MetricDistance) {
    this.id = id;
    this.groupID = groupID;
    this.artifactID = artifactID;
    this.version = version;
    this.title = title;
    this.level = level;
    this.color = color;
    this.font = font;
    this.label = `*Group Id:* ${groupID}\n*Artifact Id:* ${artifactID}\n*Version:* ${version}`;
    this.micDistance = micDistance;
    this.acDistance = acDistance;
    this.tmic = 0;
    this.tac = 0;
  }

  getLibraryCompleteName(): string {
    return `${this.groupID}.${this.artifactID}.${this.version}`;
  }

  calculateTmic(factor: number) {
    this.tmic = this.micDistance.calculateMetric(factor);
  }

  calculateTac(factor: number) {
    this.tac = this.acDistance.calculateMetric(factor);
  }
}
