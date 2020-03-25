export class DisplayEdgeModel {
    constructor(from: string, to: string) {
        this.from = from;
        this.to = to;
    }

    from: string;
    to: string;
    weight: number;
    arrows: string;
    label: string;
    font: any;

    setDefaultFont(): void {
        this.font = { size: 12, color: "red", face: "sans", background: "white" };
    }

    set edgeWeight(weight: number) {
        this.weight = weight;
        this.label = `[${weight}]`;
    }

    setArrowsDirection() {
        this.arrows = "to";
    }
}
