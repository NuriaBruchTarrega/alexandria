import {TreeNode} from './node';
import {TreeEdge} from './edge';

export class DependencyTreeFactory {
  static create({nodes = [], edges = []}): DependencyTree {
    return new DependencyTree(nodes, edges);
  }
}

export interface IDependencyTree {
  nodes: TreeNode[];
  edges: TreeEdge[];
}

export class DependencyTree implements IDependencyTree {
  private _nodes: TreeNode[];
  private _edges: TreeEdge[];

  constructor(nodes: TreeNode[], edges: TreeEdge[]) {
    this._nodes = nodes;
    this._edges = edges;
  }

  get nodes(): TreeNode[] {
    return this._nodes;
  }

  set nodes(value: TreeNode[]) {
    this._nodes = value;
  }

  get edges(): TreeEdge[] {
    return this._edges;
  }

  set edges(value: TreeEdge[]) {
    this._edges = value;
  }
}
