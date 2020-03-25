import { NetworkParsingException } from '../errors/NetworkParsingException';
import { DisplayNetworkModel } from '../models/display/display-network.model';

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

    private parseIntProperty(property: any, exMessage: string): number {
        let intProp = parseInt(property, 10);
        if (isNaN(intProp)) {
          throw new NetworkParsingException(exMessage);
        }
    
        return intProp;
      }
}
