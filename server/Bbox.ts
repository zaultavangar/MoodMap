export class Bbox {
  public readonly bottom: number;
  public readonly left: number;
  public readonly top: number;
  public readonly right: number;

  // throw some errors if individual numbers are invalid
  constructor(bottom: number, left: number, top: number, right: number) {
    this.bottom = bottom;
    this.left = left;
    this.top = top;
    this.right = right;
  }
}