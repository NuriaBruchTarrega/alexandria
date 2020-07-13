import {TreeNode} from './node';

export class DependencyTreeFactory {
  static create({nodes = [], edges = []}): DependencyTree {
    return new DependencyTree(nodes, edges);
  }
}

export interface IDependencyTree {
  nodes: TreeNode[];
  edges: IEdge[];
}

export interface IEdge {
  from: number;
  to: number;
}

export class DependencyTree implements IDependencyTree {
  private _nodes: TreeNode[];
  private _edges: IEdge[];

  constructor(nodes: TreeNode[], edges: IEdge[]) {
    this._nodes = nodes;
    this._edges = edges;
  }

  get nodes(): TreeNode[] {
    return this._nodes;
  }

  set nodes(value: TreeNode[]) {
    this._nodes = value;
  }

  get edges(): IEdge[] {
    return this._edges;
  }

  set edges(value: IEdge[]) {
    this._edges = value;
  }
}
