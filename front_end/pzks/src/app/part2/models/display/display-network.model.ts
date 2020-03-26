import * as vis from 'vis';

export class DisplayNetworkModel {
    nodes: vis.DataSet;
    edges: vis.DataSet;
    isDirected: boolean;
    isNodesHasWeight: boolean;
    isEdgesHasWeight: boolean;
}
