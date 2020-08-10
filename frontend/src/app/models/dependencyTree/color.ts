export class NodeColorFactory {
  static create(level: number, bloated: boolean): NodeColor {
    const baseColor = level === 0 ?
      '#e91e63' : bloated ?
        '#cccccc' : level === 1 ?
          '#7986cb' : '#9fa8da';
    const accent = new Color('#ff80ab', '#ff80ab');
    return new NodeColor(baseColor, baseColor, accent, accent);
  }
}

export class NodeColor {
  border: string;
  background: string;
  highlight: Color;
  hover: Color;

  constructor(border: string, background: string, highlight: Color, hover: Color) {
    this.border = border;
    this.background = background;
    this.highlight = highlight;
    this.hover = hover;
  }
}

export class Color {
  border: string;
  background: string;

  constructor(border, background) {
    this.border = border;
    this.background = background;
  }
}
