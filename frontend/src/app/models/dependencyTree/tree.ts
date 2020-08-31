import {isNil} from 'lodash';
import {TreeNode} from './node';
import {TreeEdge} from './edge';
import {IdType} from 'vis-network';

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

  getNodeById(id: IdType): TreeNode {
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

  getNodeBranch(id: IdType) {
    this._nodes.forEach(node => node.hidden = true);
    this._edges.forEach(edge => edge.hidden = true);

    this.displayAncestors(id);
    this.displayDescendants(id);
  }

  displayAllTree() {
    this._nodes.forEach(node => node.setHidden(false));
    this._edges.forEach(edge => edge.setHidden(false));
  }

  getNotHiddenNodeIds() {
    return this._nodes.filter(node => !node.hidden).map(node => node.id.toString());
  }

  private displayAncestors(id: IdType) {
    const queue: (IdType)[] = [id];

    while (queue.length !== 0) {
      const current = queue.pop();
      this._nodes.forEach(node => node.id === current && node.setHidden(false));

      const edgeToCurrent: TreeEdge = this._edges.find(edge => edge.to === current);
      if (!isNil(edgeToCurrent)) {
        edgeToCurrent.setHidden(false);
        queue.push(edgeToCurrent.from);
      }
    }
  }
  private displayDescendants(id: IdType) {
    const queue: (IdType)[] = [id];

    while (queue.length !== 0) {
      const current = queue.pop();
      this._nodes.forEach(node => node.id === current && node.setHidden(false));

      const edgesFromCurrent: TreeEdge[] = this._edges.filter(edge => edge.from === current);
      edgesFromCurrent.forEach(edgeFromCurrent => {
        edgeFromCurrent.setHidden(false);
        queue.push(edgeFromCurrent.to);
      });
    }
  }
}
