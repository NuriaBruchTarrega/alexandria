import {isNil} from 'lodash';
import {TreeNode, TreeNodeFactory} from '../models/dependencyTree/node';
import {TreeEdge, TreeEdgeFactory} from '../models/dependencyTree/edge';
import {DependencyTreeFactory} from '../models/dependencyTree/tree';
import {buildTooltipContent} from './tooltip.builder';
import {NodeColorFactory} from '../models/dependencyTree/color';
import {schema} from './result-schema';
import Ajv from 'ajv';
import {MetricDistanceFactory} from '../models/dependencyTree/metric.distance';
import {ClassDistributionFactory} from '../models/dependencyTree/class.distribution';

export function buildDependencyGraph(res) {
  if (!validateJson(res)) {
    return;
  }
  const clientLibraryNode = res.dependencyTreeResult;
  const {nodes, edges}: { nodes: TreeNode[], edges: TreeEdge[] } = traverseTree(clientLibraryNode);
  return DependencyTreeFactory.createFromObjects(nodes, edges);
}

function validateJson(res): boolean {
  const ajv = new Ajv();
  const validator = ajv.compile(schema);
  const isValid = validator(res);
  if (!isValid) {
    throw new Error(`Invalid response scheme:\n ${validator.errors.map(err => `${err.message}\n`)}`);
  }
  return !!isValid;
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
    const newNode: TreeNode = createNode(visiting, id);
    nodes.push(newNode);
    if (!isNil(visiting.parentId)) {
      edges.push(TreeEdgeFactory.create({from: visiting.parentId, to: id}));
    }

    visiting.children.forEach(child => {
      child.parentId = id;
      child.parentLevel = newNode.level;
      toVisit.push(child);
    });
  }

  return {nodes, edges};
}

function createNode(visiting: any, id: number): TreeNode {
  const level = isNil(visiting.parentLevel) ? 0 : visiting.parentLevel + 1;
  const title = buildTooltipContent(visiting.micAtDistance, visiting.acAtDistance);
  const {groupID, artifactID, version} = visiting.library;
  const micDistance = MetricDistanceFactory.create(visiting.micAtDistance);
  const acDistance = MetricDistanceFactory.create(visiting.acAtDistance);
  const micClassDistribution = ClassDistributionFactory.create(visiting.micClassDistribution);
  const acClassDistribution = ClassDistributionFactory.create(visiting.acClassDistribution);
  return TreeNodeFactory.create({
    id,
    groupID,
    artifactID,
    version,
    title: level !== 0 ? title : '',
    level,
    color: NodeColorFactory.create(level),
    micDistance,
    acDistance,
    micClassDistribution,
    acClassDistribution
  });
}
