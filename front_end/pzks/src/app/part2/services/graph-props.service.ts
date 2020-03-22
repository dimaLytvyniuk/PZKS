import { Injectable } from '@angular/core';
import * as vis from 'vis';
import { GraphColor } from '../models/graph-color';
import { DisplayNetworkModel } from '../models/display-network-model';
import { NodeModel } from '../models/nodeModel';
import { GraphConnectionType } from '../models/graph-connection-type';

@Injectable({
  providedIn: 'root'
})
export class GraphPropsService {

  constructor() { }

  public isCyclicGraph(graph: DisplayNetworkModel): Boolean {
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
    let nodesMap = this.getNodesMap(graph.nodes);
    let edjes = graph.edges;
    let nodeToNodesMap = this.getNodesToNodesMap(graph.nodes);

    graph.nodes.forEach(node => {
      let nodeColors = this.getNodeColors(graph.nodes);
      let connectedNodes = this.dfsGraphConnectionType(node, nodesMap, edjes, nodeColors);
      
      connectedNodes.forEach(connectedNodeId => {
        nodeToNodesMap.get(node.id).set(connectedNodeId, true);
      });
    });

    console.log(nodeToNodesMap);

    let isStronglyConnected = true;
    let isWeaklyConnected = true;
    
    graph.nodes.forEach(node => {
      graph.nodes.forEach(targetNode => {
        if (node.id != targetNode.id) {
          let isNodeToTargetNode = nodeToNodesMap.get(node.id).get(targetNode.id);
          let isTargetNodeToNode = nodeToNodesMap.get(targetNode.id).get(node.id);
          
          if (!isNodeToTargetNode && !isTargetNodeToNode) {
            isStronglyConnected = false;
            isWeaklyConnected = false;
            console.log(`${node.id} ${targetNode.id}`)
          } 
          else if (isNodeToTargetNode || isTargetNodeToNode) {
            isStronglyConnected = false;
          }
        }
      });
    });

    if (isStronglyConnected) {
      return GraphConnectionType.StronglyConnected;
    }

    if (isWeaklyConnected) {
      return GraphConnectionType.WeaklyConnected;
    }

    return GraphConnectionType.NotConnected;
  }

  private dfsIsCyclic(node: NodeModel, nodesMap: Map<string, any>, edges: vis.Dataset, nodeColors: Map<string, GraphColor>): Boolean {
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

  private dfsGraphConnectionType(
      node: NodeModel,
      nodesMap: Map<string, any>, 
      edges: vis.Dataset, 
      nodeColors: Map<string, GraphColor>): Array<string> {
    
    nodeColors.set(node.id, GraphColor.Grey);
    let connectedNodes = new Array<string>();
    let adjacentNodes = this.getAdjacentNodesIds(node, edges);

    for (let adjacentNodeId in adjacentNodes) {
      let adjacentNode = nodesMap.get(adjacentNodes[adjacentNodeId]);
      let adjacentNodeColor = nodeColors.get(adjacentNode.id);

      if (adjacentNodeColor === GraphColor.White) {
        let adjacentNodeConnectedNodes = this.dfsGraphConnectionType(adjacentNode, nodesMap, edges, nodeColors);
        connectedNodes.push(adjacentNode.id);
        connectedNodes = connectedNodes.concat(adjacentNodeConnectedNodes);
      } else if (adjacentNodeColor === GraphColor.Grey) {
        connectedNodes.push(adjacentNode.id);
      }
    }

    return connectedNodes;
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

  private getAdjacentNodesIds(node: NodeModel, edges: vis.DataSet): Array<string> {
    let adjacentNodes = new Array<string>();
    edges.forEach(edge => {
      if (edge.from === node.id) {
        adjacentNodes.push(edge.to);
      }
    })

    return adjacentNodes;
  }

  private getUndirectedAdjacentNodesIds(node: NodeModel, edges: vis.DataSet): Array<string> {
    let adjacentNodes = this.getAdjacentNodesIds(node, edges);
    
    edges.forEach(edge => {
      if (edge.to === node.id) {
        if (!adjacentNodes.find(x => edge.from)) {
          adjacentNodes.push(edge.from);
        }
      }
    })

    return adjacentNodes;
  }

  private getNodesMap(nodes: vis.DataSet): Map<string, any> {
    let nodesMap = new Map<string, any>();
    nodes.forEach(node => {
      nodesMap.set(node.id, node);
    });
    
    return nodesMap;
  }

  private getNodesToNodesMap(nodes: vis.DataSet): Map<string, Map<string, Boolean>> {
    let nodeToNodesMap = new Map<string, Map<string, Boolean>>();
    
    nodes.forEach(node => {
      let adjacentNodes = new Map<string, Boolean>();
      nodes.forEach(node1 => {
        adjacentNodes.set(node1.id, false);
      })

      nodeToNodesMap.set(node.id, adjacentNodes);
    });

    return nodeToNodesMap;
  }
}
