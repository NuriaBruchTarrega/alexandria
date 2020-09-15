import {isNil} from 'lodash';
import {TreeNode, TreeNodeFactory} from '@models/dependencyTree/node';
import {TreeEdge, TreeEdgeFactory} from '@models/dependencyTree/edge';
import {DependencyTreeFactory} from '@models/dependencyTree/tree';
import {schema} from './result-schema';
import Ajv from 'ajv';
import {MetricDistance, MetricDistanceFactory} from '@models/dependencyTree/metric.distance';
import {ClassDistribution, ClassDistributionFactory} from '@models/dependencyTree/class.distribution';

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
  const level: number = isNil(visiting.parentLevel) ? 0 : visiting.parentLevel + 1;
  const {groupID, artifactID, version} = visiting.library;
  const micDistance: MetricDistance = MetricDistanceFactory.create(visiting.micAtDistance, 'MIC');
  const acDistance: MetricDistance = MetricDistanceFactory.create(visiting.acAtDistance, 'AC');
  const callsDistribution: ClassDistribution = ClassDistributionFactory.create(visiting.micClassDistribution);
  const fieldsDistribution: ClassDistribution = ClassDistributionFactory.create(visiting.acClassDistribution);
  const {unused} = visiting;
  const classUsage: number = calculatePercentage(visiting.numReachableClasses, visiting.numClasses);
  const methodUsage: number = calculatePercentage(visiting.numReachableBehaviors, visiting.numBehaviors);
  return TreeNodeFactory.create({
    id, groupID, artifactID, version, level,
    micDistance, acDistance,
    callsDistribution, fieldsDistribution,
    unused, classUsage, methodUsage
  });
}

function calculatePercentage(part: number, total: number): number {
  return +((part / total) * 100).toFixed(2);
}
