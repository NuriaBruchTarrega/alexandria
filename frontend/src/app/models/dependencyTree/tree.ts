import {TreeNode} from './node';
import {TreeEdge} from './edge';

export class DependencyTreeFactory {
  static createFromObjects(nodes: TreeNode[], edges: TreeEdge[]): DependencyTree {
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

  getAllNodeIds(): string[] {
    return this._nodes.map(node => node.id.toString());
  }

  numNodes(): number {
    return this._nodes.length;
  }

  getNodeById(id: number) {
    return this._nodes.find(node => node.id === id);
  }

  getLibrariesCompleteNames(): string[] {
    return this._nodes.map(node => node.getLibraryCompleteName());
  }

  getNodeIdWithLibraryCompleteName(libraryName): number {
    return this._nodes.find(node => node.getLibraryCompleteName() === libraryName).id;
  }

  calculateTMIC(factor: number) {
    this._nodes.forEach(node => node.calculateTmic(factor));
  }

  calculateTAC(factor: number) {
    this._nodes.forEach(node => node.calculateTac(factor));
  }
}
