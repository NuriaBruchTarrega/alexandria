export class TreeEdgeFactory {
  static create({from = 0, to = 0}): TreeEdge {
    return new TreeEdge(from, to);
  }
}

export interface ITreeEdge {
  from: number;
  to: number;
}

export class TreeEdge implements ITreeEdge {
  private _from: number;
  private _to: number;

  constructor(from: number, to: number) {
    this._from = from;
    this._to = to;
  }

  get from(): number {
    return this._from;
  }

  set from(value: number) {
    this._from = value;
  }

  get to(): number {
    return this._to;
  }

  set to(value: number) {
    this._to = value;
  }
}
