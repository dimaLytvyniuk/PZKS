import { Component, OnInit, Input, EventEmitter, Output, ViewChild, ElementRef } from '@angular/core';
import { DisplayNetworkModel } from '../../models/display/display-network.model';
import { GraphConnectionType } from '../../models/graph-connection-type';
import { NetworkParsingException } from '../../errors/NetworkParsingException';
import * as vis from 'vis';
import { DirectedGraphManipulationService } from '../../services/directed-graph-manipulation.service';
import { BaseGraphManipulationService } from '../../services/base-graph-manipulation.service';
import { UndirectedGraphManipulationService } from '../../services/undirected-graph-manipulation.service';

@Component({
  selector: 'app-graph-general',
  templateUrl: './graph-general.component.html',
  styleUrls: ['./graph-general.component.css']
})
export class GraphGeneralComponent implements OnInit {
  private readonly notConnectedGraphMessage = "Граф не зв'язаний";
  private readonly weaklyConnectedGraphMessage = "Граф слабозв'язаний";
  private readonly stronglyConnectedGraphMessage = "Граф сильнозв'язаний";
  private readonly undirectedConnectedGraphMessage = "Граф зв'язаний"
  
  nodes = null;
  edges = null;
  network = null;

  @Input() data: DisplayNetworkModel;
  @Input() isDirected: boolean = false;
  @Input() isNodesHasWeight: boolean = false;
  @Input() isEdgesHasWeight: boolean = false;
  
  @Output() graphChanged = new EventEmitter<DisplayNetworkModel>();

  @ViewChild('network') networkRef: ElementRef;
  @ViewChild('operationSpan') operationSpanRef: ElementRef;
  @ViewChild('labelData') labelDataRef: ElementRef;
  @ViewChild('nodeLabel') nodeLabelRef: ElementRef;
  @ViewChild('nodeWeight') nodeWeightRef: ElementRef;
  @ViewChild('saveButton') saveButtonRef: ElementRef;
  @ViewChild('cancelButton') cancelButtonRef: ElementRef;
  @ViewChild('netoworkPopUp') netoworkPopUpRef: ElementRef;

  networkElement: HTMLElement;
  operationSpanElement: HTMLElement;
  labelDataElement: HTMLElement;
  nodeLabelElement: HTMLElement;
  nodeWeightElement: HTMLElement;
  saveButtonElement: HTMLElement;
  cancelButtonElement: HTMLElement;
  netoworkPopUpElement: HTMLElement;

  graphManipulationService: BaseGraphManipulationService;

  seed = 2;
  
  nodeName = "";
  nodeWeight = "";

  connectionLabel = "";
  cyclicLabel = "";
  
  constructor(
    private directedGraphManipulationService: DirectedGraphManipulationService,
    private undirectedGraphManipulationService: UndirectedGraphManipulationService
  ) {
  }

  ngOnInit() {
    this.networkElement = this.networkRef.nativeElement as HTMLElement;
    this.operationSpanElement = this.operationSpanRef.nativeElement as HTMLElement;
    this.labelDataElement = this.labelDataRef.nativeElement as HTMLElement;
    this.nodeLabelElement = this.nodeLabelRef.nativeElement as HTMLElement;
    this.nodeWeightElement = this.nodeWeightRef.nativeElement as HTMLElement;
    this.saveButtonElement = this.saveButtonRef.nativeElement as HTMLElement;
    this.cancelButtonElement = this.cancelButtonRef.nativeElement as HTMLElement;
    this.netoworkPopUpElement = this.netoworkPopUpRef.nativeElement as HTMLElement;

    if (this.isDirected) {
      this.graphManipulationService = this.directedGraphManipulationService;
    } else {
      this.graphManipulationService = this.undirectedGraphManipulationService;
    }

    this.draw();
    this.data.isDirected = this.isDirected;
    this.data.isEdgesHasWeight = this.isEdgesHasWeight;
    this.data.isNodesHasWeight = this.isNodesHasWeight;
  }

  destroy() {
    if (this.network !== null) {
      this.network.destroy();
      this.network = null;
    }
  }

  clearPopUp() {
    this.saveButtonElement.onclick = null;
    this.cancelButtonElement.onclick = null;
    this.labelDataElement.style.visibility = "visible";
    this.netoworkPopUpElement.style.display = "none";
    this.nodeLabelElement.setAttribute("value", "");
    this.nodeWeightElement.setAttribute("value", "");

    this.nodeName = "";
  }
  
  cancelEdit(callback) {
    this.clearPopUp();
    callback(null);
  }
  
  saveNode = (data, callback) => {
    let weight = this.getNodeWeight();
    let newNode = this.graphManipulationService.getNewNode(data, this.nodeName, weight, this.data);
  
    this.clearPopUp();
    callback(newNode);
    
    this.onDataChanged();
  }

  saveEdge = (data, callback) => {
    let weight = this.getEdgeWeight();
    let newEdge = this.graphManipulationService.getNewEdge(data, this.nodeWeight, this.data);

    this.clearPopUp();
    callback(newEdge);

    this.onDataChanged();
  }

  draw() {
    this.destroy();
    this.nodes = [];
    this.edges = [];
  
    // create a network
    var options = {
      layout: { randomSeed: this.seed }, // just to make sure the layout is the same when the locale is changed
      //locale: document.getElementById("locale").nodeValue,
      manipulation: {
        addNode: (data, callback) => this.onAddNode(data, callback),
        editNode: (data, callback) => this.onEditNode(data, callback),
        addEdge: (data, callback) => this.onAddEdge(data, callback),
        deleteNode: (data, callback) => this.onDeleteNode(data, callback),
        deleteEdge: (data, callback) => this.onDeleteEdge(data, callback)
      }
    };

    this.network = new vis.Network(this.networkElement, this.data, options);
    this.onDataChanged();
  }

  private onAddNode = (data, callback) => {
    // filling in the popup DOM elements
    this.operationSpanElement.innerHTML = "Add Node";
    this.netoworkPopUpElement.style.display = "block";
    this.nodeLabelElement.setAttribute("value", "");
    
    if (this.isNodesHasWeight) {
      this.nodeWeightElement.setAttribute("value", "");
    } else {
      this.nodeWeightElement.style.visibility = "hidden";
    }

    data.id = null;
    this.saveButtonElement.onclick = () => this.saveNode(data,callback);
    this.cancelButtonElement.onclick = () => this.clearPopUp();
  }

  private onEditNode = (data, callback) => {
    if (!this.isNodesHasWeight) {
      callback(data);
      return;
    }

    this.operationSpanElement.innerHTML = "Edit Node";
    this.netoworkPopUpElement.style.display = "block";
    this.labelDataElement.style.visibility = "hidden";
    
    this.nodeWeight = data.weight;
    this.nodeWeightElement.setAttribute("value", data.weight);

    this.saveButtonElement.onclick = () => this.saveNode(data,callback);
    this.cancelButtonElement.onclick = () => this.cancelEdit(callback);
  }

  private onAddEdge = (data, callback) => {
    if (this.isEdgesHasWeight) {
      this.operationSpanElement.innerHTML = "Add Edge";
      this.netoworkPopUpElement.style.display = "block";
      this.labelDataElement.style.visibility = "hidden";
      this.nodeWeightElement.setAttribute("value", "");
  
      this.saveButtonElement.onclick = () => this.saveEdge(data,callback);
      this.cancelButtonElement.onclick = () => this.cancelEdit(callback);
    } else {
      this.saveEdge(data, callback);
    }
  }

  private onDeleteNode = (data, callback) => {
    callback(data);
    this.onDataChanged();
  }

  private onDeleteEdge = (data, callback) => {
    callback(data);
    this.onDataChanged();
  }

  onChagedNodeLabelBox($event) {
    this.nodeName = $event.target.value;
    console.log(this.nodeName);
  }

  onChagedNodeWeightBox($event) {
    this.nodeWeight = $event.target.value;
  }

  saveNetwork() {
    let networkModel = this.graphManipulationService.getStoreNetworkModel(this.data);

    localStorage.setItem("network", JSON.stringify(networkModel));
  }

  onSaveToFile() {
    let networkModel = this.graphManipulationService.getStoreNetworkModel(this.data);
    console.log(networkModel);

    this.graphManipulationService.saveObjectToFile(networkModel, "graph-task.json");
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
    
    let networkModel = this.graphManipulationService.parseObjectToDisplayNetwork(object);

    return networkModel;
  }

  onDataChanged() {
    this.graphChanged.emit(this.data);
    
    let isCyclicGraph = this.graphManipulationService.isCyclicGraph(this.data);
    let connectionType = this.graphManipulationService.getGraphConnectionType(this.data);
    
    this.setCyclicLabel(isCyclicGraph);
    this.setConnectionLabel(connectionType);

    this.saveNetwork();
  }

  setConnectionLabel(connectionType: GraphConnectionType): void {
    switch (connectionType) {
      case GraphConnectionType.NotConnected:
        this.connectionLabel = this.notConnectedGraphMessage;
        break
      case GraphConnectionType.WeaklyConnected:
        this.connectionLabel = this.isDirected ? this.weaklyConnectedGraphMessage : this.undirectedConnectedGraphMessage;
        break;
      case GraphConnectionType.StronglyConnected:
        this.connectionLabel = this.isDirected ? this.stronglyConnectedGraphMessage : this.undirectedConnectedGraphMessage;
        break;
    }
   }

  setCyclicLabel(isCyclicGraph: boolean): void {
    if (isCyclicGraph) {
      this.cyclicLabel = "Граф має циклічну ділянку";
    } else {
      this.cyclicLabel = "Граф ациклічний";
    }
  }

  private getNodeWeight(): number {
    if (this.isNodesHasWeight) {
      return this.parseIntProperty(this.nodeWeight, "Weight should be int");
    }

    return undefined;
  }

  private getEdgeWeight() {
    if (this.isEdgesHasWeight) {
      this.parseIntProperty(this.nodeWeight, "Weight should be int");
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
