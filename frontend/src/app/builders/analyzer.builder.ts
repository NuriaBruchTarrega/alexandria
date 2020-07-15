import {isNil} from 'lodash';
import {TreeNode, TreeNodeFactory} from '../models/dependencyTree/node';
import {TreeEdge, TreeEdgeFactory} from '../models/dependencyTree/edge';
import {DependencyTreeFactory} from '../models/dependencyTree/tree';

export function buildDependencyGraph(res) {
  const clientLibraryNode = res.dependencyTreeResult;
  const {nodes, edges}: { nodes: TreeNode[], edges: TreeEdge[] } = traverseTree(clientLibraryNode);
  return DependencyTreeFactory.createFromObjects(nodes, edges);
}

function traverseTree(clientLibraryNode: any): { nodes: TreeNode[], edges: TreeEdge[] } {
  const toVisit = [];
  toVisit.push(clientLibraryNode);

  const nodes: TreeNode[] = [];
  const edges: TreeEdge[] = [];
  let id = -1;

  while (toVisit.length !== 0) {
    id += 1;
    const [visiting] = toVisit.splice(0, 1);

    // Create node
    const level = isNil(visiting.parentLevel) ? 0 : visiting.parentLevel + 1;
    const title = createTitleFromMetrics(visiting.micAtDistance, visiting.acAtDistance);
    nodes.push(TreeNodeFactory.create({
      id,
      label: createNodeLabelFromLibrary(visiting.library),
      title: level !== 0 ? title : '',
      level,
      color: calculateColorFromLevel(level)
    }));
    if (!isNil(visiting.parentId)) {
      edges.push(TreeEdgeFactory.create({from: visiting.parentId, to: id}));
    }

    visiting.children.forEach(child => {
      child.parentId = id;
      child.parentLevel = level;
      toVisit.push(child);
    });
  }

  return {nodes, edges};
}

function createNodeLabelFromLibrary(library: any): string {
  const groupID = library.groupID;
  const artifactID = library.artifactID;
  const version = library.version;

  return `*Group Id:* ${groupID}\n*Artifact Id:* ${artifactID}\n*Version:* ${version}`;
}

function createTitleFromMetrics(micAtDistance: any, acAtDistance: any): string {
  let title = 'MIC (distance: value)';
  for (const distance in micAtDistance) {
    if (micAtDistance.hasOwnProperty(distance)) {
      title = title.concat(`<br>${distance}: ${micAtDistance[distance]}`);
    }
  }

  title += '<br><br>AC (distance: value)';
  for (const distance in acAtDistance) {
    if (acAtDistance.hasOwnProperty(distance)) {
      title = title.concat(`<br>${distance}: ${acAtDistance[distance]}`);
    }
  }

  return title;
}

function calculateColorFromLevel(level: number): string {
  return level === 0 ? '#5c6bc0' : level === 1 ? '#7986cb' : '#9fa8da';
}
