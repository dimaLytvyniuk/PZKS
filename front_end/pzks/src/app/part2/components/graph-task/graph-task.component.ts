import { Component, OnInit } from '@angular/core';
import * as vis from 'vis';
import { NodeModel } from '../../models/nodeModel';
import { StoreNetworkModel } from '../../models/store-network-model';
import { EdgeModel } from '../../models/edgeModel';
import { NetworkParsingException } from '../../errors/NetworkParsingException';
import { GraphPropsService } from '../../services/graph-props.service';
import { DisplayNetworkModel } from '../../models/display-network-model';

@Component({
  selector: 'app-graph-task',
  templateUrl: './graph-task.component.html',
  styleUrls: ['./graph-task.component.css']
})
export class GraphTaskComponent implements OnInit {
  nodes = null;
  edges = null;
  network = null;

  data: DisplayNetworkModel = this.getDefaultData();
  seed = 2;
  
  nodeName = "";
  nodeWeight = "";

  constructor(private graphPropsService: GraphPropsService) { }

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

      let isExists = this.data.nodes.getIds().find(x => x.toString() === data.id);
      if (isExists) {
        throw new NetworkParsingException("Node with the same id is already exists");
      }
    }

    data.weight = this.parseIntProperty(this.nodeWeight, "Weight should be int");
    data.label = `${data.id} [${data.weight}]`;

    this.clearPopUp();
    callback(data);
    
    this.onDataChanged();
  }

  saveEdge = (data, callback) => {
    data.arrows = "to";
    data.weight = this.parseIntProperty(this.nodeWeight, "Weight should be int");;
    data.label = `[${data.weight}]`;
    data.font = { size: 12, color: "red", face: "sans", background: "white" };

    this.clearPopUp();
    callback(data);

    this.onDataChanged();
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
          this.onDataChanged();
        },
        deleteEdge: (data, callback) => {
          callback(data);
          this.onDataChanged();
        }
      }
    };

    this.network = new vis.Network(container, this.data, options);
    this.onDataChanged();
  }

  getDefaultData(): DisplayNetworkModel {
    var nodes = new vis.DataSet([
      { id: "1", label: "1 [1]", weight: 1 },
      { id: "2", label: "2 [2]", weight: 2 },
      { id: "3", label: "3 [2]", weight: 2 },
      { id: "4", label: "4 [3]", weight: 3 },
      { id: "5", label: "5 [4]", weight: 4 }
    ]);
  
    // create an array with edges
    var edges = new vis.DataSet([
      { from: "1", to: "3", arrows: "to", label: "[3]", font: { size: 12, color: "red", face: "sans", background: "white" }, weight: "1" },
      { from: "1", to: "2", arrows: "to", label: "[4]" },
      { from: "2", to: "4", arrows: "to", label: "[5]" },
      { from: "2", to: "5", arrows: "to", label: "[6]" },
      { from: "3", to: "3", arrows: "to", label: "[6]" }
    ]);

    var data = new DisplayNetworkModel();
    data.nodes = nodes;
    data.edges = edges;

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
    let networkModel = this.getStoreNetworkModel();

    localStorage.setItem("network", JSON.stringify(networkModel));
  }

  getStoreNetworkModel(): StoreNetworkModel {
    let networkModel = new StoreNetworkModel();
    networkModel.nodes = this.getNodesToStore();
    networkModel.edges = this.getEdgesToStore();

    return networkModel;
  }

  getNodesToStore(): NodeModel[] {
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

  getEdgesToStore(): EdgeModel[] {
    let edges: EdgeModel[] = new Array<EdgeModel>();
    let objectKeys = Object.keys(this.network.clustering.body.edges);

    for (let i in objectKeys) {
      let networkEdge = this.network.clustering.body.edges[objectKeys[i]];

      let edgeLabel = networkEdge.options.label;
      let edgeWeight = parseInt(edgeLabel.substring(1, edgeLabel.length));
      let edge = new EdgeModel(networkEdge.fromId, networkEdge.toId, edgeWeight);

      edges.push(edge);
    }

    return edges;
  }

  onSaveToFile() {
    let networkModel = this.getStoreNetworkModel();

    this.saveFile(networkModel, "graph-task.json");
  }

  saveFile(jObject, fileName) {
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

  onFileSelected($event) {
    let files = $event.target.files;
    if (files.length == 0) {
      return;
    }

    let file = files[0];
    let reader = new FileReader();
    reader.addEventListener('load', ($event) => this.onReadedDataFromFile($event));
    reader.readAsText(file);
  }

  onReadedDataFromFile($event) {
    let fileData = $event.target.result;

    this.data = this.parseStringToDisplayNetwork(fileData);
    console.log(this.data);

    this.draw();
  }

  parseStringToDisplayNetwork(fileData: string): DisplayNetworkModel {
    let object = JSON.parse(fileData);
    let networkModel = new DisplayNetworkModel();
    networkModel.nodes = this.parseObjectToNodes(object.nodes);
    networkModel.edges = this.parseObjectToEdges(object.edges);

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

      let nodeModel = new NodeModel();
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
      let edgeModel = new EdgeModel(objectEdges[i].from, objectEdges[i].to, weight);

      edgeModels.add(edgeModel);
    }

    return edgeModels;
  }

  validateProperty(property: any, exMessage: string) {
    if (property === undefined || property === null) {
      throw new NetworkParsingException(exMessage);
    }
  }

  parseIntProperty(property: any, exMessage: string): number {
    let intProp = parseInt(property, 10);
    if (isNaN(intProp)) {
      throw new NetworkParsingException(exMessage);
    }

    return intProp;
  }

  onDataChanged() {
    console.log(this.graphPropsService.isCyclicGraph(this.data));
    console.log(this.graphPropsService.getGraphConnectionType(this.data));
    this.saveNetwork();
  }
}
