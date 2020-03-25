import { Injectable } from '@angular/core';
import * as vis from 'vis';
import { GraphColor } from '../models/graph-color';
import { DisplayNetworkModel } from '../models/display/display-network.model';
import { DisplayNodeModel } from '../models/display/display-node.model';
import { GraphConnectionType } from '../models/graph-connection-type';
import { TimeObject } from '../models/time-object';
import { DisplayEdgeModel } from '../models/display/display-edge.model';

@Injectable({
  providedIn: 'root'
})
export class GraphPropsService {

  constructor() { }

  public isCyclicGraph(graph: DisplayNetworkModel): boolean {
    let nodesMap = this.getNodesMap(graph.nodes);
    let edjes = graph.edges;
    let nodeColors = this.getNodeColors(graph.nodes);
  
    let whiteNode = nodesMap.get(this.getWhiteNode(nodeColors));
    
    while (whiteNode != null) {
      if (this.dfsIsCyclic(whiteNode, nodesMap, edjes, nodeColors)) {
        return true;
      }

      whiteNode = nodesMap.get(this.getWhiteNode(nodeColors));
    }

    return false;
  }

  public getGraphConnectionType(graph: DisplayNetworkModel): GraphConnectionType {
    let isConnectedUnirected = this.isConnectedUnirected(graph);
    if (!isConnectedUnirected) {
      return GraphConnectionType.NotConnected;
    }

    let nodesMap = this.getNodesMap(graph.nodes);
    let edjes = graph.edges;
    let nodeColors = this.getNodeColors(graph.nodes);
    let timeMap = new Map<number, string>();
    let time = new TimeObject(0);

    let whiteNode = nodesMap.get(this.getWhiteNode(nodeColors));
    
    while (whiteNode != null) {
      this.dfsGraphConnectionType(whiteNode, nodesMap, edjes, nodeColors, timeMap, time);

      whiteNode = nodesMap.get(this.getWhiteNode(nodeColors));
    }

    let reversedEdges = this.getReverseEdges(edjes);
    let sortedTimes = Array.from(timeMap.keys()).sort().reverse();
    nodeColors = this.getNodeColors(graph.nodes);
    let newTimeMap = new Map<number, string>();
    time = new TimeObject(0);
    let N = 0;
    
    sortedTimes.forEach(sortedTime => {
      let currentNodeId = timeMap.get(sortedTime);

      if (nodeColors.get(currentNodeId) === GraphColor.White) {
        N++;
        this.dfsGraphConnectionType(nodesMap.get(currentNodeId), nodesMap, reversedEdges, nodeColors, newTimeMap, time);
      }
    });

    if (N === 1) {
      return GraphConnectionType.StronglyConnected;
    } else {
      return GraphConnectionType.WeaklyConnected;
    }
  }

  private isConnectedUnirected(graph: DisplayNetworkModel): boolean {
    let N = 0;
    let nodesMap = this.getNodesMap(graph.nodes);
    let edjes = graph.edges;
    let nodeColors = this.getNodeColors(graph.nodes);

    let whiteNode = nodesMap.get(this.getWhiteNode(nodeColors));
    
    while (whiteNode != null) {
      this.bfsUndirectedGraphConnectionType(whiteNode, nodesMap, edjes, nodeColors);
      N += 1; 
      whiteNode = nodesMap.get(this.getWhiteNode(nodeColors));
    }

    if (N == 1) {
      return true;
    } else {
      return false;
    }
  }

  private dfsIsCyclic(node: DisplayNodeModel, nodesMap: Map<string, any>, edges: vis.Dataset, nodeColors: Map<string, GraphColor>): boolean {
    nodeColors.set(node.id, GraphColor.Grey);
    let adjacentNodes = this.getAdjacentNodesIds(node.id, edges);
    
    for (let adjacentNodeId in adjacentNodes) {
      let adjacentNode = nodesMap.get(adjacentNodes[adjacentNodeId]);
      let adjacentNodeColor = nodeColors.get(adjacentNode.id);

      if (adjacentNodeColor === GraphColor.White) {
        if (this.dfsIsCyclic(adjacentNode, nodesMap, edges, nodeColors)) {
          return true;
        }
      } 
      else if (adjacentNodeColor === GraphColor.Grey) {
        return true;
      }
    }

    nodeColors.set(node.id, GraphColor.Black);
    return false;
  }

  private dfsGraphConnectionType(
      node: DisplayNodeModel,
      nodesMap: Map<string, any>, 
      edges: vis.Dataset, 
      nodeColors: Map<string, GraphColor>,
      timeMap: Map<number, string>,
      time: TimeObject
  ): void {
     let adjacentNodesIds = this.getAdjacentNodesIds(node.id, edges);

     nodeColors.set(node.id, GraphColor.Grey);

     adjacentNodesIds.forEach(adjacentNodeId => {
      if (nodeColors.get(adjacentNodeId) === GraphColor.White) {
        this.dfsGraphConnectionType(nodesMap.get(adjacentNodeId), nodesMap, edges, nodeColors, timeMap, time);
      }
     });

     timeMap.set(time.value, node.id);
     time.value++;
  }

  private bfsUndirectedGraphConnectionType(
    node: DisplayNodeModel,
    nodesMap: Map<string, any>, 
    edges: vis.Dataset, 
    nodeColors: Map<string, GraphColor>
  ): void {
    let nodeQueue = new Array<string>();
    nodeQueue.push(node.id);

    while (nodeQueue.length > 0) {
      let currentNode = nodeQueue.pop();
      nodeColors.set(currentNode, GraphColor.Grey);
      let adjacentNodes = this.getUndirectedAdjacentNodesIds(currentNode, edges);
      
      adjacentNodes.forEach(adjacentNodeId => {
        if (nodeColors.get(adjacentNodeId) === GraphColor.White) {
          nodeQueue.push(adjacentNodeId);
        }
      });
    }
  }

  private getWhiteNode(nodeColors: Map<string, GraphColor>): string {
    let whiteNode = null;
    
    nodeColors.forEach((color, nodeId) => {
      if (color == GraphColor.White) {
        whiteNode = nodeId;
      }
    });

    return whiteNode;
  }

  private getNodeColors(nodes: vis.DataSet): Map<string, GraphColor> {
    let nodeColors = new Map<string, GraphColor>();
    nodes.forEach(element => {
      nodeColors.set(element.id, GraphColor.White);
    });

    return nodeColors;
  }

  private getAdjacentNodesIds(nodeId: string, edges: vis.DataSet): Array<string> {
    let adjacentNodes = new Array<string>();
    edges.forEach(edge => {
      if (edge.from === nodeId) {
        adjacentNodes.push(edge.to);
      }
    });

    return adjacentNodes;
  }

  private getUndirectedAdjacentNodesIds(nodeId: string, edges: vis.DataSet): Array<string> {
    let adjacentNodes = this.getAdjacentNodesIds(nodeId, edges);
    
    edges.forEach(edge => {
      if (edge.to === nodeId) {
        if (!adjacentNodes.find(x => x === edge.from)) {
          adjacentNodes.push(edge.from);
        }
      }
    });

    return adjacentNodes;
  }

  private getNodesMap(nodes: vis.DataSet): Map<string, any> {
    let nodesMap = new Map<string, any>();
    nodes.forEach(node => {
      nodesMap.set(node.id, node);
    });
    
    return nodesMap;
  }

  private getNodesToNodesMap(nodes: vis.DataSet): Map<string, Map<string, boolean>> {
    let nodeToNodesMap = new Map<string, Map<string, boolean>>();
    
    nodes.forEach(node => {
      let adjacentNodes = new Map<string, boolean>();
      nodes.forEach(node1 => {
        adjacentNodes.set(node1.id, false);
      })

      nodeToNodesMap.set(node.id, adjacentNodes);
    });

    return nodeToNodesMap;
  }

  private getReverseEdges(edges: vis.DataSet): vis.DataSet {
    let reverseEdges = new vis.DataSet();

    edges.forEach(edge => {
      let reversedEdge = new DisplayEdgeModel(edge.to, edge.from);
      reversedEdge.weight = edge.weight;

      reverseEdges.add(reversedEdge);
    });

    return reverseEdges;
  }
}
