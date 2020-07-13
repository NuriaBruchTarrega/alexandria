import {TreeNode, TreeNodeFactory} from '../models/dependencyTree/node';
import {TreeEdge, TreeEdgeFactory} from '../models/dependencyTree/edge';
import {DependencyTreeFactory} from '../models/dependencyTree/tree';

export function buildDependencyGraph(res) {
  const clientLibraryNode = res.mic;
  const {nodes, edges}: { nodes: TreeNode[], edges: TreeEdge[] } = iterateTreeToCreateGraph(clientLibraryNode);
  return DependencyTreeFactory.createFromObjects(nodes, edges);
}

function iterateTreeToCreateGraph(clientLibraryNode: any): { nodes: TreeNode[], edges: TreeEdge[] } {
  let nodes: TreeNode[] = [];
  let edges: TreeEdge[] = [];
  const id = 0;

  const clientNode = TreeNodeFactory.create({id, label: getLibraryName(clientLibraryNode.library)});
  nodes.push(clientNode);

  const {nodes: childNodes, edges: childEdges}: { nodes: TreeNode[], edges: TreeEdge[] } = recursiveFunction(clientLibraryNode, id);
  nodes = nodes.concat(childNodes);
  edges = edges.concat(childEdges);

  return {nodes, edges};
}

function recursiveFunction(rootLibrary: any, rootLibraryNodeId: number): { nodes: TreeNode[], edges: TreeEdge[] } {
  let nodes: TreeNode[] = [];
  let edges: TreeEdge[] = [];

  let id = rootLibraryNodeId;
  const children: any[] = rootLibrary.children;

  children.forEach(libraryDep => {
    id += 1;
    const libraryNode = TreeNodeFactory.create({id, label: getLibraryName(libraryDep.library)});
    nodes.push(libraryNode);
    edges.push(TreeEdgeFactory.create({from: rootLibraryNodeId, to: id}));

    if (libraryDep.children.length !== 0) {
      const {nodes: childNodes, edges: childEdges}: { nodes: TreeNode[], edges: TreeEdge[] } = recursiveFunction(libraryDep, id);
      nodes = nodes.concat(childNodes);
      edges = edges.concat(childEdges);
    }

  });

  return {nodes, edges};
}

function getLibraryName(library: any): string {
  const groupID = library.groupID;
  const artifactID = library.artifactID;
  const version = library.version;

  return `${groupID}.${artifactID}.${version}`;
}
