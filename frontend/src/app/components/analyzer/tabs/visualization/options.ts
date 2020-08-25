import {BLUE} from '@src/colors';

export const options = {
  interaction: {
    hover: true,
    dragNodes: false,
    selectable: true,
    selectConnectedEdges: true,
  },
  manipulation: {
    enabled: false
  },
  layout: {
    hierarchical: {
      sortMethod: 'directed',
      direction: 'UD',
      nodeSpacing: 400,
      levelSeparation: 150
    }
  },
  physics: {
    enabled: false,
    hierarchicalRepulsion: {
      avoidOverlap: 1
    }
  },
  nodes: {
    shape: 'ellipse'
  },
  edges: {
    color: BLUE
  }
};
