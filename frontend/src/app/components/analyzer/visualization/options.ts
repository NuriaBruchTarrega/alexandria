export const options = {
  interaction: {
    hover: true,
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
    repulsion: {
      nodeDistance: 300
    }
  }
};
