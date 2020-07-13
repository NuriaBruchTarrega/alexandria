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
  from: number;
  to: number;

  constructor(from: number, to: number) {
    this.from = from;
    this.to = to;
  }
}
