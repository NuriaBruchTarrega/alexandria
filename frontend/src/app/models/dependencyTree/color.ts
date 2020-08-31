import {Colors, Hue} from '@src/colors';
import * as convert from 'color-convert';

const {PINK, GREY, LIGHT_PINK} = Colors;
const {HUE_MAX, HUE_MIN} = Hue;

export class NodeColorFactory {
  static create(level: number, unused: boolean, percentage: number): NodeColor {
    const baseColor = level === 0 ? PINK : unused ? GREY : this.percentageToColor(percentage, HUE_MAX, HUE_MIN);
    const accent = new Color(LIGHT_PINK, LIGHT_PINK);
    return new NodeColor(baseColor, baseColor, accent, accent);
  }

  private static percentageToColor(percentage: number, maxHue: number, minHue: number) {
    const hue = (percentage / 100) * (maxHue - minHue) + minHue;
    const color = convert.hsl.hex(hue, 100, 50);
    return `#${color}`;
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
