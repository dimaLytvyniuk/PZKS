import { NetworkParsingException } from '../errors/NetworkParsingException';
import { DisplayNetworkModel } from '../models/display/display-network.model';
import { StoreNetworkModel } from '../models/store/store-network-model';
import { StoreNodeModel } from '../models/store/store-node.model';
import { StoreEdgeModel } from '../models/store/store-edge.model';
import * as vis from 'vis';
import { DisplayEdgeModel } from '../models/display/display-edge.model';
import { DisplayNodeModel } from '../models/display/display-node.model';

export abstract class BaseGraphManipulationService {
  constructor() { }

  getNewNode (
    newNodeObject: any, 
    nodeName: string,
    nodeWeight: number,
    graph: DisplayNetworkModel
    ): any {
    console.log(newNodeObject);
    if (newNodeObject.id == null) {
        newNodeObject.id = nodeName

      let isExists = graph.nodes.getIds().find(x => x.toString() === newNodeObject.id);
      if (isExists) {
        throw new NetworkParsingException("Node with the same id is already exists");
      }
    }
    
    if (graph.isNodesHasWeight) {
        newNodeObject.weight = nodeWeight;
        newNodeObject.label = `${newNodeObject.id} [${newNodeObject.weight}]`;
    } else {
        newNodeObject.label = `${newNodeObject.id}`;
    }

    return newNodeObject;
}

  abstract getNewEdge(
    newEdgeObject: any,
    edgeWeight: string,
    graph: DisplayNetworkModel
  ): any;

  getStoreNetworkModel(graph: DisplayNetworkModel): StoreNetworkModel {
    let networkModel = new StoreNetworkModel();
    networkModel.isDirected = graph.isDirected;
    networkModel.isEdgesHasWeight = graph.isEdgesHasWeight;
    networkModel.isNodesHasWeight = graph.isNodesHasWeight;
    networkModel.nodes = this.getNodesToStore(graph);
    networkModel.edges = this.getEdgesToStore(graph);

    return networkModel;
  }

  getNodesToStore(graph: DisplayNetworkModel): StoreNodeModel[] {
    let nodes: StoreNodeModel[] = new Array();

    graph.nodes.forEach(graphNode => {
      let node = new StoreNodeModel();
      node.id = graphNode.id;
      node.label = graphNode.label;
      if (graph.isNodesHasWeight) {
        node.weight = parseInt(graphNode.weight);
      }

      nodes.push(node);
    });

    return nodes;
  }

  getEdgesToStore(graph: DisplayNetworkModel): StoreEdgeModel[] {
    let edges: StoreEdgeModel[] = new Array<StoreEdgeModel>();

    graph.edges.forEach(graphEdge => {
      let edge = new StoreEdgeModel(graphEdge.from, graphEdge.to);
      if (graph.isEdgesHasWeight) {
        edge.weight = parseInt(graphEdge.weight);
      }

      edges.push(edge);
    });

    return edges;
  }

  saveObjectToFile(jObject, fileName: string): void {
    var a = document.createElement("a");
    document.body.appendChild(a);
    a.style.display = "none";

    let json = JSON.stringify(jObject, null, '\t');
    let blob = new Blob([json], { type: "octet/stream" });
    let url = window.URL.createObjectURL(blob);

    a.href = url;
    a.download = fileName;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  protected createBaseModelFromObject(jObject: any): DisplayNetworkModel {
    if (jObject.isDirected === undefined) {
      throw new NetworkParsingException(`Graph from file doesn't have isDirected property`);
    }

    if (jObject.isNodesHasWeight === undefined) {
      throw new NetworkParsingException("Graph from file doesn't have isNodeHasWeight property");
    }

    if (jObject.isEdgesHasWeight === undefined) {
      throw new NetworkParsingException("Graph from file doesn't have isEdgesHasWeight property");
    }

    let networkModel = new DisplayNetworkModel();
    networkModel.isDirected = jObject.isDirected;
    networkModel.isNodesHasWeight = jObject.isNodesHasWeight;
    networkModel.isEdgesHasWeight = jObject.isEdgesHasWeight;

    return networkModel;
  }

  parseObjectToDisplayNetwork(jObject: any): DisplayNetworkModel {
    let networkModel = this.createBaseModelFromObject(jObject);
    networkModel.nodes = this.parseObjectToNodes(jObject.nodes, networkModel);
    networkModel.edges = this.parseObjectToEdges(jObject.edges, networkModel);

    return networkModel;
  }

  parseObjectToNodes(objectNodes: any[], networkModel: DisplayNetworkModel): vis.DataSet {
    let nodeModels = new vis.DataSet();
    this.validateProperty(objectNodes, "Nodes aren't exist in parsed object");

    for (let i in objectNodes) {
      this.validateProperty(objectNodes[i].id, `In node ${i} property 'id' isn't exist`);
      this.validateProperty(objectNodes[i].label, `In node ${i} property 'label' isn't exist`);
      if (networkModel.isNodesHasWeight) {
        this.validateProperty(objectNodes[i].weight, `In node ${i} property 'weight' isnt exist`);
      }

      let nodeModel = new DisplayNodeModel();
      nodeModel.id = objectNodes[i].id;
      nodeModel.label = objectNodes[i].label;
      
      if (networkModel.isNodesHasWeight) {
        let weight = this.parseIntProperty(objectNodes[i].weight, `In node ${i} property 'weight' has incorrect value`);
        nodeModel.weight = weight;  
      }
      
      nodeModels.add(nodeModel);
    }

    return nodeModels;
  }

  abstract parseObjectToEdges(objectEdges: any[], networkModel: DisplayNetworkModel): vis.DataSet;

  protected validateProperty(property: any, exMessage: string) {
    if (property === undefined || property === null) {
      throw new NetworkParsingException(exMessage);
    }
  }

  protected parseIntProperty(property: any, exMessage: string): number {
    let intProp = parseInt(property, 10);
    if (isNaN(intProp)) {
      throw new NetworkParsingException(exMessage);
    }

    return intProp;
  }
}
