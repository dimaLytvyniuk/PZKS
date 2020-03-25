export class StoreEdgeModel {
    constructor(from: string, to: string) {
        this.from = from;
        this.to = to;
    }

    from: string;
    to: string;
    weight: number;
}
