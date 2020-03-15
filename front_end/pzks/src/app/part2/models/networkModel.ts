import { NodeModel } from './nodeModel';
import { EdgeModel } from './edgeModel';
import * from vis;

export class NetworkModel {
    nodes: vis.Dataset<NodeModel>;
    edges: EdgeModel[];
}
