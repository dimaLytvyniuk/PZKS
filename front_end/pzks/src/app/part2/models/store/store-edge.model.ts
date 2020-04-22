export class StoreEdgeModel {
    constructor(from: string, to: string, weight?: number) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    from: string;
    to: string;
    weight: number;
}
