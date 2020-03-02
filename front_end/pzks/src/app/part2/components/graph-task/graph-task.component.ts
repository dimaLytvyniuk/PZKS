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
    document.getElementById("network-popUp").style.display = "none";
  }
  
  cancelEdit(callback) {
    this.clearPopUp();
    callback(null);
  }
  
  saveData = (data, callback) => {
    data.id = document.getElementById("node-id").nodeValue;
    //data.id = document.getElementById("node-id").value;
    data.label = document.getElementById("node-label").getAttribute("value");
    console.log(document.getElementById("node-id").nodeValue);
    this.clearPopUp();
    callback(data);
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
          document.getElementById("node-id").nodeValue = data.id;
          document.getElementById("node-label").nodeValue = data.label;
          document.getElementById("saveButton").onclick = () => this.saveData(data,callback);
          document.getElementById("cancelButton").onclick = () => this.clearPopUp();
          document.getElementById("network-popUp").style.display = "block";
        },
        editNode: (data, callback) => {
          // filling in the popup DOM elements
          document.getElementById("operation").innerHTML = "Edit Node";
          document.getElementById("node-id").nodeValue = data.id;
          document.getElementById("node-label").nodeValue = data.label;
          document.getElementById("saveButton").onclick = () => { this.saveData(data,callback); }
          document.getElementById("cancelButton").onclick = () => this.cancelEdit(callback);
          document.getElementById("network-popUp").style.display = "block";
        },
        addEdge: function(data, callback) {
          if (data.from == data.to) {
            var r = confirm("Do you want to connect the node to itself?");
            if (r == true) {
              callback(data);
            }
          } else {
            callback(data);
          }
        }
      }
    };
    this.network = new vis.Network(container, this.data, options);
  }

  getDefaultData() {
    var nodes = new vis.DataSet([
      { id: 1, label: "Node 1" },
      { id: 2, label: "Node 2" },
      { id: 3, label: "Node 3" },
      { id: 4, label: "Node 4" },
      { id: 5, label: "Node 5" }
    ]);
  
    // create an array with edges
    var edges = new vis.DataSet([
      { from: 1, to: 3 },
      { from: 1, to: 2 },
      { from: 2, to: 4 },
      { from: 2, to: 5 },
      { from: 3, to: 3 }
    ]);

    var data = {
      nodes: nodes,
      edges: edges
    };

    return data;
  }
}
