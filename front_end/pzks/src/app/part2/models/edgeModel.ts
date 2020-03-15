export class EdgeModel {
    constructor(from: string, to: string, weight: number) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        
        this.arrows = "to";
        this.font = { size: 12, color: "red", face: "sans", background: "white" };
        this.label = `[${weight}]`;
    }

    from: string;
    to: string;
    weight: number;
    arrows: string;
    label: string;
    font: any;
}
