export class TreeNodeFactory {
  static create({id = 0, label = '', title = '', level = 0, color= '', font = {multi: 'md'}}): TreeNode {
    return new TreeNode(id, label, title, level, color, font);
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

  constructor(id: number, label: string, title: string, level: number, color: string, font: any) {
    this.id = id;
    this.label = label;
    this.title = title;
    this.level = level;
    this.color = color;
    this.font = font;
  }
}
