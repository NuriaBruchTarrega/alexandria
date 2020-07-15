export class TreeNodeFactory {
  static create({id = 0, groupID = '', artifactID = '', version = '', title = '', level = 0, color = '', font = {multi: 'md'}}): TreeNode {
    return new TreeNode(id, groupID, artifactID, version, title, level, color, font);
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
  color: string;
  font: any;
  groupID: string;
  artifactID: string;
  version: string;

  constructor(id: number, groupID: string, artifactID: string, version: string, title: string, level: number, color: string, font: any) {
    this.id = id;
    this.groupID = groupID;
    this.artifactID = artifactID;
    this.version = version;
    this.title = title;
    this.level = level;
    this.color = color;
    this.font = font;
    this.label = `*Group Id:* ${groupID}\n*Artifact Id:* ${artifactID}\n*Version:* ${version}`;
  }

  getLibraryCompleteName(): string {
    return `${this.groupID}.${this.artifactID}.${this.version}`;
  }
}
