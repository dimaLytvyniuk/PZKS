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
      networkModel.isDirected = graph.isDirected;
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

    saveObjectToFile(jObject, fileName: string): void {
      var a = document.createElement("a");
      document.body.appendChild(a);
      a.style.display = "none";
      
      let json = JSON.stringify(jObject,null, '\t');
      let blob = new Blob([json], {type: "octet/stream"});
      let url = window.URL.createObjectURL(blob);

      a.href = url;
      a.download = fileName;
      a.click();
      window.URL.revokeObjectURL(url);
    }

    isDirectedGraph(jObject: any): boolean {
      if (jObject.isDirected === undefined) {
        throw new NetworkParsingException(`Graph from file doen't have isDirected property`);
      }

      return jObject.isDirected;
    }

    parseObjectToDisplayNetwork(jObject: any): DisplayNetworkModel {
      let networkModel = new DisplayNetworkModel();
      networkModel.nodes = this.parseObjectToNodes(jObject.nodes);
      networkModel.edges = this.parseObjectToEdges(jObject.edges);

      return networkModel;
    }

    parseObjectToNodes(objectNodes: any[]): vis.DataSet {
      let nodeModels = new vis.DataSet();
      this.validateProperty(objectNodes, "Nodes aren't exist in parsed object");
  
      for (let i in objectNodes) {
        this.validateProperty(objectNodes[i].id, `In node ${i} property 'id' isn't exist`);
        this.validateProperty(objectNodes[i].label, `In node ${i} property 'label' isn't exist`);
        this.validateProperty(objectNodes[i].weight, `In node ${i} property 'weight' isnt exist`);
  
        let weight = this.parseIntProperty(objectNodes[i].weight, `In node ${i} property 'weight' has incorrect value`);
  
        let nodeModel = new DisplayNodeModel();
        nodeModel.id = objectNodes[i].id;
        nodeModel.label = objectNodes[i].label;
        nodeModel.weight = weight;
  
        nodeModels.add(nodeModel);
      }
  
      return nodeModels;
    }
  
    parseObjectToEdges(objectEdges: any[]): vis.DataSet {
      let edgeModels = new vis.DataSet();
      this.validateProperty(objectEdges, "Edges aren't exist in parsed object");
  
      for (let i in objectEdges) {
        this.validateProperty(objectEdges[i].from, `In edge ${i} property 'from' isn't exist`);
        this.validateProperty(objectEdges[i].to, `In edge ${i} property 'to' isn't exist`);
        this.validateProperty(objectEdges[i].weight, `In edge ${i} property 'weight' isn't exist`);
        
        let weight = this.parseIntProperty(objectEdges[i].weight, `In edge ${i} property 'weight' has incorrect value`);
        let edgeModel = new DisplayEdgeModel(objectEdges[i].from, objectEdges[i].to);
        edgeModel.edgeWeight = weight;
        edgeModel.setArrowsDirection();
        edgeModel.setDefaultFont();
        
        edgeModels.forEach(exictedEdge => {
          if (exictedEdge.from === edgeModel.from && exictedEdge.to === edgeModel.to) {
            throw new NetworkParsingException(`Edge from ${edgeModel.from} to ${edgeModel.to} added more than once in file`);
          }
        });
  
        edgeModels.add(edgeModel);
      }
  
      return edgeModels;
    }
  
    validateProperty(property: any, exMessage: string) {
      if (property === undefined || property === null) {
        throw new NetworkParsingException(exMessage);
      }
    }

    private parseIntProperty(property: any, exMessage: string): number {
        let intProp = parseInt(property, 10);
        if (isNaN(intProp)) {
          throw new NetworkParsingException(exMessage);
        }
    
        return intProp;
      }
}
