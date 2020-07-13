export const options = {
  interaction: {
    hover: true,
    dragNodes: true,
    selectable: true,
    selectConnectedEdges: true,
    tooltipDelay: 100,
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
