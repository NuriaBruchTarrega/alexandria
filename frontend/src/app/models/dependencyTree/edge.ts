export class TreeEdgeFactory {
  static create({from = 0, to = 0}): TreeEdge {
    return new TreeEdge(from, to);
  }
}

export interface ITreeEdge {
  from: number;
  to: number;
  hidden: boolean;
}

export class TreeEdge implements ITreeEdge {
  from: number;
  to: number;
  hidden: boolean;

  constructor(from: number, to: number) {
    this.from = from;
    this.to = to;
    this.hidden = false;
  }

  setHidden(value: boolean) {
    this.hidden = value;
  }
}
