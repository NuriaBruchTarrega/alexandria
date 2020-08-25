import {GREY, LIGHT_BLUE, LIGHT_PINK, LIGHTER_BLUE, PINK} from '@src/colors';

export class NodeColorFactory {
  static create(level: number, bloated: boolean): NodeColor {
    const baseColor = level === 0 ?
      PINK : bloated ? GREY : level === 1 ? LIGHT_BLUE : LIGHTER_BLUE;
    const accent = new Color(LIGHT_PINK, LIGHT_PINK);
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
