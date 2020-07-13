export class TreeNodeFactory {
  static create({id = 0, label = '', title = ''}): TreeNode {
    return new TreeNode(id, label, title);
  }
}

export interface ITreeNode {
  id: number;
  label: string;
  title: string;
}

export class TreeNode implements ITreeNode {
  id: number;
  label: string;
  title: string;

  constructor(id: number, label: string, title: string) {
    this.id = id;
    this.label = label;
    this.title = title;
  }
}
