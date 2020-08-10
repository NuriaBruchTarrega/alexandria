export const schema = {
  type: 'object',
  properties: {
    dependencyTreeResult: {
      type: 'object',
      properties: {
        library: {
          type: 'object',
          properties: {
            groupID: {
              type: 'string'
            },
            artifactID: {
              type: 'string'
            },
            version: {
              type: 'string'
            }
          },
          required: ['groupID', 'artifactID', 'version']
        },
        micAtDistance: {
          type: 'object'
        },
        acAtDistance: {
          type: 'object'
        },
        annotationsAtDistance: {
          type: 'object'
        },
        micClassDistribution: {
          type: 'object'
        },
        acClassDistribution: {
          type: 'object'
        },
        children: {
          type: 'array',
          items: {$ref: '#/properties/dependencyTreeResult'}
        },
        bloated: {
          type: 'boolean'
        }
      },
      required: ['library', 'micAtDistance', 'acAtDistance', 'children']
    }
  },
  required: ['dependencyTreeResult']
};
