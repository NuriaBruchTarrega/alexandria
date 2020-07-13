import {TreeNode, TreeNodeFactory} from '../models/dependencyTree/node';
import {TreeEdge, TreeEdgeFactory} from '../models/dependencyTree/edge';
import {DependencyTreeFactory} from '../models/dependencyTree/tree';

export function buildDependencyGraph(res) {
  const clientLibraryNode = res.mic;
  const {nodes, edges}: { nodes: TreeNode[], edges: TreeEdge[] } = traverseTree(clientLibraryNode);
  return DependencyTreeFactory.createFromObjects(nodes, edges);
}

function traverseTree(clientLibraryNode: any): { nodes: TreeNode[], edges: TreeEdge[] } {
  const toVisit = [];
  toVisit.push(clientLibraryNode);

  const nodes: TreeNode[] = [];
  const edges: TreeEdge[] = [];
  let id = 0;

  while (toVisit.length !== 0) {
    id += 1;
    const [visiting] = toVisit.splice(0, 1);

    // Create node
    nodes.push(TreeNodeFactory.create({id, label: getLibraryName(visiting.library)}));
    if (visiting.parentId) {
      edges.push(TreeEdgeFactory.create({from: visiting.parentId, to: id}));
    }

    visiting.children.forEach(child => {
      child.parentId = id;
      toVisit.push(child);
    });
  }

  return {nodes, edges};
}

function getLibraryName(library: any): string {
  const groupID = library.groupID;
  const artifactID = library.artifactID;
  const version = library.version;

  return `${groupID}.${artifactID}.${version}`;
}
