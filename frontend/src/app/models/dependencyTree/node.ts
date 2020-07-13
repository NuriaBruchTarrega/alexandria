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
  private _id: number;
  private _label: string;
  private _title: string;

  constructor(id: number, label: string, title: string) {
    this._id = id;
    this._label = label;
    this._title = title;
  }

  get id(): number {
    return this._id;
  }

  set id(value: number) {
    this._id = value;
  }

  get label(): string {
    return this._label;
  }

  set label(value: string) {
    this._label = value;
  }

  get title(): string {
    return this._title;
  }

  set title(value: string) {
    this._title = value;
  }
}
