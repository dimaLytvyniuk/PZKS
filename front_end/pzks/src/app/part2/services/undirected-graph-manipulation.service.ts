import { Injectable } from '@angular/core';
import { BaseGraphManipulationService } from './base-graph-manipulation.service';
import { DisplayNetworkModel } from '../models/display/display-network.model';
import { NetworkParsingException } from '../errors/NetworkParsingException';
import * as vis from 'vis';
import { DisplayEdgeModel } from '../models/display/display-edge.model';
import { GraphPropsService } from './graph-props.service';
import { GraphConnectionType } from '../models/graph-connection-type';

@Injectable({
  providedIn: 'root'
})
export class UndirectedGraphManipulationService extends BaseGraphManipulationService {

  constructor(private graphPropsService: GraphPropsService) {
    super();
  }

  getNewEdge(
    newEdgeObject: any,
    edgeWeight: string,
    graph: DisplayNetworkModel
  ): any {
    graph.edges.forEach(edge => {
      if ((edge.from === newEdgeObject.from && edge.to === newEdgeObject.to) ||
        (edge.to === newEdgeObject.from && edge.from === newEdgeObject.to)) {
        throw new NetworkParsingException(`Edge from ${newEdgeObject.from} to ${newEdgeObject.to} is already exists`);
      }
    });

    if (graph.isEdgesHasWeight) {
      newEdgeObject.weight = this.parseIntProperty(edgeWeight, "Weight should be int");;
      newEdgeObject.label = `[${newEdgeObject.weight}]`;
      newEdgeObject.font = { size: 12, color: "red", face: "sans", background: "white" };
    }

    return newEdgeObject;
  }

  parseObjectToEdges(objectEdges: any[], networkModel: DisplayNetworkModel): vis.DataSet {
    let edgeModels = new vis.DataSet();
    this.validateProperty(objectEdges, "Edges aren't exist in parsed object");

    for (let i in objectEdges) {
      this.validateProperty(objectEdges[i].from, `In edge ${i} property 'from' isn't exist`);
      this.validateProperty(objectEdges[i].to, `In edge ${i} property 'to' isn't exist`);
      let edgeModel = new DisplayEdgeModel(objectEdges[i].from, objectEdges[i].to);

      if (networkModel.isEdgesHasWeight) {
        this.validateProperty(objectEdges[i].weight, `In edge ${i} property 'weight' isn't exist`);
        let weight = this.parseIntProperty(objectEdges[i].weight, `In edge ${i} property 'weight' has incorrect value`);

        edgeModel.edgeWeight = weight;
        edgeModel.setDefaultFont();
      }

      edgeModels.forEach(exictedEdge => {
        if ((exictedEdge.from === edgeModel.from && exictedEdge.to === edgeModel.to) ||
          (exictedEdge.to === edgeModel.from && exictedEdge.from === edgeModel.to)) {
          throw new NetworkParsingException(`Edge from ${edgeModel.from} to ${edgeModel.to} added more than once in file`);
        }
      });

      edgeModels.add(edgeModel);
    }

    return edgeModels;
  }

  public isCyclicGraph(graph: DisplayNetworkModel): boolean {
    return this.graphPropsService.isCyclicUndirectedGraph(graph);
  }

  public getGraphConnectionType(graph: DisplayNetworkModel): GraphConnectionType {
    return this.graphPropsService.getUndirectedGraphConnectionType(graph);
  }
}
