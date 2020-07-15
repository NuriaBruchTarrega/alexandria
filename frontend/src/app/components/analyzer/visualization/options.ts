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
      nodeSpacing: 300,
      levelSeparation: 100
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
  }
};
