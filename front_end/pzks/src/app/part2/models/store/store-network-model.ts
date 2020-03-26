import { StoreNodeModel } from './store-node.model';
import { StoreEdgeModel } from './store-edge.model';

export class StoreNetworkModel {
    nodes: StoreNodeModel[];
    edges: StoreEdgeModel[];
    isDirected: boolean;
    isNodesHasWeight: boolean;
    isEdgesHasWeight: boolean;
}
