export class GraphFactory {
  static create({nodes = [], edges = []}): Graph {
    return new Graph(nodes, edges);
  }
}

export interface IGraph {
  nodes: INode[];
  edges: IEdge[];
}

export interface INode {
  id: number;
  label: string;
  title: string;
}

export interface IEdge {
  from: number;
  to: number;
}

export class Graph implements IGraph {
  private _nodes: INode[];
  private _edges: IEdge[];

  constructor(nodes: INode[], edges: IEdge[]) {
    this._nodes = nodes;
    this._edges = edges;
  }

  get nodes(): INode[] {
    return this._nodes;
  }

  set nodes(value: INode[]) {
    this._nodes = value;
  }

  get edges(): IEdge[] {
    return this._edges;
  }

  set edges(value: IEdge[]) {
    this._edges = value;
  }
}
