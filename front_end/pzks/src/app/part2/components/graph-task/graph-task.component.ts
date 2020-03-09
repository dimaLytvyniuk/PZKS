import { Component, OnInit } from '@angular/core';
import * as vis from 'vis';
import { NodeModel } from '../models/nodeModel';
import { NetworkModel } from '../models/networkModel';
import { EdgeModel } from '../models/edgeModel';

@Component({
  selector: 'app-graph-task',
  templateUrl: './graph-task.component.html',
  styleUrls: ['./graph-task.component.css']
})
export class GraphTaskComponent implements OnInit {
  nodes = null;
  edges = null;
  network = null;

  data = this.getDefaultData();
  seed = 2;
  
  nodeName = "";
  nodeWeight = "";

  constructor() { }

  ngOnInit() {
    this.draw();
  }

  destroy() {
    if (this.network !== null) {
      this.network.destroy();
      this.network = null;
    }
  }

  clearPopUp() {
    document.getElementById("saveButton").onclick = null;
    document.getElementById("cancelButton").onclick = null;
    document.getElementById("label-data").style.visibility = "visible";
    document.getElementById("network-popUp").style.display = "none";
    document.getElementById("node-label").setAttribute("value", "");
    document.getElementById("node-weight").setAttribute("value", "");

    this.nodeName = "";
  }
  
  cancelEdit(callback) {
    this.clearPopUp();
    callback(null);
  }
  
  saveData = (data, callback) => {
    console.log(data);
    if (data.id == null) {
      data.id = this.nodeName;
    }
    
    data.weight = this.nodeWeight;
    data.label = `${data.id} [${data.weight}]`;
    
    this.clearPopUp();
    callback(data);
    console.log(this.network.clustering.body.nodes);
    return 0;
  }

  saveEdge = (data, callback) => {
    data.arrows = "to";
    data.weight = this.nodeWeight;
    data.label = `[${data.weight}]`;
    data.font = { size: 12, color: "red", face: "sans", background: "white" };
    this.clearPopUp();

    callback(data);
  }

  draw() {
    this.destroy();
    this.nodes = [];
    this.edges = [];
  
    // create a network
    var container = document.getElementById("mynetwork");
    var options = {
      layout: { randomSeed: this.seed }, // just to make sure the layout is the same when the locale is changed
      //locale: document.getElementById("locale").nodeValue,
      manipulation: {
        addNode: (data, callback) => {
          // filling in the popup DOM elements
          document.getElementById("operation").innerHTML = "Add Node";
          document.getElementById("network-popUp").style.display = "block";
          document.getElementById("node-label").setAttribute("value", "");
          document.getElementById("node-weight").setAttribute("value", "");
          data.id = null;

          document.getElementById("saveButton").onclick = () => this.saveData(data,callback);
          document.getElementById("cancelButton").onclick = () => this.clearPopUp();
        },
        editNode: (data, callback) => {
          // filling in the popup DOM elements
          document.getElementById("operation").innerHTML = "Edit Node";
          document.getElementById("network-popUp").style.display = "block";
          document.getElementById("label-data").style.visibility = "hidden";
          
          this.nodeWeight = data.weight;
          document.getElementById("node-weight").setAttribute("value", data.weight);

          document.getElementById("saveButton").onclick = () => this.saveData(data,callback);
          document.getElementById("cancelButton").onclick = () => this.cancelEdit(callback);
        },
        addEdge: (data, callback) => {
          document.getElementById("operation").innerHTML = "Add Edge";
          document.getElementById("network-popUp").style.display = "block";
          document.getElementById("label-data").style.visibility = "hidden";
          document.getElementById("node-weight").setAttribute("value", "");

          document.getElementById("saveButton").onclick = () => this.saveEdge(data,callback);
          document.getElementById("cancelButton").onclick = () => this.cancelEdit(callback);
        },
        deleteNode: (data, callback) => {
          callback(data);
          console.log(Object.keys(this.network.clustering.body.nodes));
        },
        deleteEdge: (data, callback) => {
          callback(data);
          console.error(this.network.clustering.body.edges);
          console.error(this.network.clustering.body.nodes);
          this.saveNetwork();
        }
      }
    };
    this.network = new vis.Network(container, this.data, options);
  }

  getDefaultData() {
    var nodes = new vis.DataSet([
      { id: 1, label: "1 [1]", weight: 1 },
      { id: 2, label: "2 [2]", weight: 2 },
      { id: 3, label: "3 [2]", weight: 2 },
      { id: 4, label: "4 [3]", weight: 3 },
      { id: 5, label: "5 [4]", weight: 4 }
    ]);
  
    // create an array with edges
    var edges = new vis.DataSet([
      { from: 1, to: 3, arrows: "to", label: "[3]", font: { size: 12, color: "red", face: "sans", background: "white" }, weight: "1" },
      { from: 1, to: 2, arrows: "to", label: "[4]" },
      { from: 2, to: 4, arrows: "to", label: "[5]" },
      { from: 2, to: 5, arrows: "to", label: "[6]" },
      { from: 3, to: 3, arrows: "to", label: "[6]" }
    ]);

    var data = {
      nodes: nodes,
      edges: edges
    };

    return data;
  }

  onChagedNodeLabelBox($event) {
    this.nodeName = $event.target.value;
    console.log(this.nodeName);
  }

  onChagedNodeWeightBox($event) {
    this.nodeWeight = $event.target.value;
  }

  saveNetwork() {
    let networkModel = this.getJsonObject();

    localStorage.setItem("network", JSON.stringify(networkModel));
  }

  getJsonObject(): NetworkModel {
    let networkModel = new NetworkModel();
    networkModel.edges = this.getEdges();
    networkModel.nodes = this.getNodes();

    return networkModel;
  }

  getNodes(): NodeModel[] {
    let nodes: NodeModel[] = new Array();
    let objectKeys = Object.keys(this.network.clustering.body.nodes);

    for (let i in objectKeys) {
      if (objectKeys[i].startsWith("edgeId")) {
        break;
      }

      let networkNode = this.network.clustering.body.nodes[objectKeys[i]];
      let node = new NodeModel();
      node.id = networkNode.id;
      node.label = networkNode.options.label;
      node.weight = parseInt(networkNode.options.weight, 10);

      nodes.push(node);
    }

    return nodes;
  }

  getEdges(): EdgeModel[] {
    let edges: EdgeModel[] = new Array<EdgeModel>();
    let objectKeys = Object.keys(this.network.clustering.body.edges);

    for (let i in objectKeys) {
      let networkEdge = this.network.clustering.body.edges[objectKeys[i]];
      let edge = new EdgeModel();

      edge.from = networkEdge.fromId;
      edge.to = networkEdge.toId;
      
      let edgeLabel = networkEdge.options.label;
      edge.weight = parseInt(edgeLabel.substring(1, edgeLabel.length));

      edges.push(edge);
    }

    return edges;
  }
}
