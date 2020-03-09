import { Component, OnInit } from '@angular/core';
import * as vis from 'vis';

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
          data.arrows = "to";
          callback(data);
          // if (data.from == data.to) {
          //   var r = confirm("Do you want to connect the node to itself?");
          //   if (r == true) {
          //     callback(data);
          //   }
          // } else {
          //   callback(data);
          // }
        },
        deleteNode: (data, callback) => {
          callback(data);
          console.log(Object.keys(this.network.clustering.body.nodes));
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
      { from: 1, to: 3, arrows: "to" },
      { from: 1, to: 2, arrows: "to" },
      { from: 2, to: 4, arrows: "to" },
      { from: 2, to: 5, arrows: "to" },
      { from: 3, to: 3, arrows: "to" }
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
}
