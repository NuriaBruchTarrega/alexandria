export class TreeNodeFactory {
  static create({id = 0, label = '', title = '', font = {multi: 'md'}}): TreeNode {
    return new TreeNode(id, label, title, font);
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
  font: any;

  constructor(id: number, label: string, title: string, font: any) {
    this.id = id;
    this.label = label;
    this.title = title;
    this.font = font;
  }
}
