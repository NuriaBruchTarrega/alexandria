import {isNil} from 'lodash';
import {TreeNode, TreeNodeFactory} from '../models/dependencyTree/node';
import {TreeEdge, TreeEdgeFactory} from '../models/dependencyTree/edge';
import {DependencyTreeFactory} from '../models/dependencyTree/tree';
import {buildTooltipContent} from './tooltip.builder';
import {NodeColorFactory} from '../models/dependencyTree/color';
import {schema} from './result-schema';
import Ajv from 'ajv';

export function buildDependencyGraph(res) {
  validateJson(res);
  const clientLibraryNode = res.dependencyTreeResult;
  const {nodes, edges}: { nodes: TreeNode[], edges: TreeEdge[] } = traverseTree(clientLibraryNode);
  return DependencyTreeFactory.createFromObjects(nodes, edges);
}

function validateJson(res) {
  const ajv = new Ajv();
  const validator = ajv.compile(schema);
  const isValid = validator(res);
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
    const title = buildTooltipContent(visiting.micAtDistance, visiting.acAtDistance);
    const {groupID, artifactID, version} = visiting.library;
    nodes.push(TreeNodeFactory.create({
      id,
      groupID,
      artifactID,
      version,
      title: level !== 0 ? title : '',
      level,
      color: NodeColorFactory.create(level)
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
