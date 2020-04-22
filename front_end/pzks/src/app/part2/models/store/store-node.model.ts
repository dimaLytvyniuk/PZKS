export class StoreNodeModel {
    constructor(id: string, label: string, weight?: number) {
        this.id = id;
        this.label = label;
        this.weight = weight;
    }

    id: string;
    label: string;
    weight: number;
}
