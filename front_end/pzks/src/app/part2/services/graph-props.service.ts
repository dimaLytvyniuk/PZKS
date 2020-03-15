import { Injectable } from '@angular/core';
import * as vis from 'vis';
import { GraphColor } from '../models/graph-color';

@Injectable({
  providedIn: 'root'
})
export class GraphPropsService {

  constructor() { }

  isCyclicGraph(graph): Boolean {
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

  dfsIsCyclic(node, nodesMap: Map<string, any>, edges: vis.Dataset, nodeColors: Map<string, GraphColor>): Boolean {
    nodeColors.set(node.id, GraphColor.Grey);
    let adjacentNodes = this.getAdjacentNodesIds(node, edges);
    
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

  getWhiteNode(nodeColors: Map<string, GraphColor>): string {
    let whiteNode = null;
    
    nodeColors.forEach((color, nodeId) => {
      if (color == GraphColor.White) {
        whiteNode = nodeId;
      }
    });

    return whiteNode;
  }

  getNodeColors(nodes): Map<string, GraphColor> {
    let nodeColors = new Map<string, GraphColor>();
    nodes.forEach(element => {
      nodeColors.set(element.id, GraphColor.White);
    });

    return nodeColors;
  }

  getAdjacentNodesIds(node, edges): any[] {
    let adjacentNodes = [];
    let edgesFromNode = edges.forEach(edge => {
      if (edge.from === node.id) {
        adjacentNodes.push(edge.to);
      }
    })

    return adjacentNodes;
  }

  getNodesMap(nodes): Map<string, any> {
    let nodesMap = new Map<string, any>();
    nodes.forEach(node => {
      nodesMap.set(node.id, node);
    });
    
    return nodesMap;
  }
}
