import { NetworkParsingException } from '../errors/NetworkParsingException';
import { DisplayNetworkModel } from '../models/display/display-network.model';
import { StoreNetworkModel } from '../models/store/store-network-model';
import { StoreNodeModel } from '../models/store/store-node.model';
import { StoreEdgeModel } from '../models/store/store-edge.model';
import * as vis from 'vis';

export abstract class BaseGraphManipulationService {
    constructor() { }

    getNewNode (
        newNodeObject: any, 
        nodeName: string,
        nodeWeight: string,
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
    
        newNodeObject.weight = this.parseIntProperty(nodeWeight, "Weight should be int");
        newNodeObject.label = `${newNodeObject.id} [${newNodeObject.weight}]`;

        return newNodeObject;
    }

    getNewEdge(
        newEdgeObject: any,
        edgeWeight: string,
        graph: DisplayNetworkModel
    ): any {
        graph.edges.forEach(edge => {
            if (edge.from === newEdgeObject.from && edge.to === newEdgeObject.to) {
              throw new NetworkParsingException(`Edge from ${newEdgeObject.from} to ${newEdgeObject.to} is already exists`);
            }
          });
          
          newEdgeObject.arrows = "to";
          newEdgeObject.weight = this.parseIntProperty(edgeWeight, "Weight should be int");;
          newEdgeObject.label = `[${newEdgeObject.weight}]`;
          newEdgeObject.font = { size: 12, color: "red", face: "sans", background: "white" };

          return newEdgeObject;
    }

    getStoreNetworkModel(graph: DisplayNetworkModel): StoreNetworkModel {
      let networkModel = new StoreNetworkModel();
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
        node.weight = parseInt(graphNode.weight);
  
        nodes.push(node);
      });
  
      return nodes;
    }
  
    getEdgesToStore(graph: DisplayNetworkModel): StoreEdgeModel[] {
      let edges: StoreEdgeModel[] = new Array<StoreEdgeModel>();
      
      graph.edges.forEach(graphEdge => {
        let edge = new StoreEdgeModel(graphEdge.from, graphEdge.to);
        edge.weight = parseInt(graphEdge.weight);
  
        edges.push(edge);
      });
  
      return edges;
    }

    private parseIntProperty(property: any, exMessage: string): number {
        let intProp = parseInt(property, 10);
        if (isNaN(intProp)) {
          throw new NetworkParsingException(exMessage);
        }
    
        return intProp;
      }
}
