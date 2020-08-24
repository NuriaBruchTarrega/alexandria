import {NodeColor, NodeColorFactory} from './color';
import {MetricDistance} from './metric.distance';
import {ClassDistribution} from './class.distribution';
import {buildTooltipContent} from '@builders/tooltip.builder';

export class TreeNodeFactory {
  static create({
                  id = 0, groupID = '', artifactID = '', version = '', level = 0, font = {multi: 'md'},
                  micDistance = null, acDistance = null,
                  callsDistribution = null, fieldsDistribution = null, bloated = false,
                  classUsage = 0, methodUsage = 0
                }): TreeNode {
    return new TreeNode(id, groupID, artifactID, version,
      level, font, micDistance, acDistance,
      callsDistribution, fieldsDistribution, bloated,
      classUsage, methodUsage);
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
  groupId: string;
  artifactId: string;
  version: string;
  micDistance: MetricDistance;
  acDistance: MetricDistance;
  callsDistribution: ClassDistribution;
  fieldsDistribution: ClassDistribution;
  tac: number;
  tmic: number;
  classUsage: number;
  methodUsage: number;

  constructor(id: number, groupID: string,
              artifactID: string, version: string, level: number, font: any,
              micDistance: MetricDistance, acDistance: MetricDistance,
              callsDistribution: ClassDistribution, fieldsDistribution: ClassDistribution,
              bloated: boolean, classUsage: number, methodUsage: number) {
    this.id = id;
    this.groupId = groupID;
    this.artifactId = artifactID;
    this.version = version;
    this.level = level;
    this.font = font;
    this.micDistance = micDistance;
    this.acDistance = acDistance;
    this.callsDistribution = callsDistribution;
    this.fieldsDistribution = fieldsDistribution;
    this.tmic = 0;
    this.tac = 0;
    this.classUsage = classUsage;
    this.methodUsage = methodUsage;
    this.createTitle();
    this.createLabel();
    this.calculateColor(bloated);
  }

  getLibraryCompleteName(): string {
    return `${this.groupId}.${this.artifactId}.${this.version}`;
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

  private createLabel() {
    this.label = `*Group Id:* ${this.groupId}\n*Artifact Id:* ${this.artifactId}\n*Version:* ${this.version}\n`;

    if (this.level > 1) {
      this.label += `*TMIC:* ${this.tmic} *TAC:* ${this.tac}\n`;
    } else if (this.level === 1) {
      this.label += `*MIC:* ${this.tmic} *AC:* ${this.tac}\n`;
    }

    if (this.level !== 0) {
      this.label += `*%ReachableClasses:* ${this.classUsage}\n*%ReachableMethods:* ${this.methodUsage}`;
    }
  }

  private calculateColor(bloated: boolean) {
    this.color = NodeColorFactory.create(this.level, bloated);
  }

  private createTitle() {
    this.title = buildTooltipContent(
      this.groupId + ':' + this.artifactId + ':' + this.version,
      this.micDistance, this.acDistance, this.classUsage, this.methodUsage);
  }
}
