import {Colors} from '@src/colors';

const {PINK, GREY, LIGHT_BLUE, LIGHTER_BLUE, LIGHT_PINK} = Colors;

export class NodeColorFactory {
  static create(level: number, used: boolean): NodeColor {
    const baseColor = level === 0 ?
      PINK : used ? GREY : level === 1 ? LIGHT_BLUE : LIGHTER_BLUE;
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
