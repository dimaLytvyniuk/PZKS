import { Component, OnInit } from '@angular/core';
import { OutputParsedExpression } from 'src/app/models/outputParsedExpression';
import { InputExpression } from 'src/app/models/inputExpression';
import { BackendClientService } from 'src/app/services/backend-client.service';
import { HttpErrorResponse } from '@angular/common/http/http';
import * as vis from 'vis';
import { TreeBuilderService } from 'src/app/services/tree-builder.service';
import { ExpressionTree } from 'src/app/models/expressionTree';

@Component({
  selector: 'app-lab1',
  templateUrl: './lab1.component.html',
  styleUrls: ['./lab1.component.css']
})
export class Lab1Component implements OnInit {
  evaluatedResult: string = null
  errors: string = null
  outputResult: OutputParsedExpression;
  inputExpression: string;

  constructor(
    private backEndClient: BackendClientService,
    private treeBuilderService: TreeBuilderService) { }

  ngOnInit() {
    
  }

  onChangeExpressionField(event) {
    this.inputExpression = event.target.value;
  }

  onSubmit() {
    this.errors = null
    this.evaluatedResult = null
    this.getParsedExpression()
  }

  getParsedExpression() {
    let inputModel = new InputExpression();
    inputModel.expression = this.inputExpression;

    this.backEndClient.lab1(inputModel).subscribe(data => {
      console.log(data)
      this.outputResult = data
      if (this.outputResult.exceptionModel != null)
        this.errors = this.outputResult.exceptionModel.message
      if (this.outputResult.evaluatedResult != null)
        this.evaluatedResult = this.outputResult.evaluatedResult
      
      this.drawTree(data.expressionTree);
      console.log(this.evaluatedResult)
    },
    (err: HttpErrorResponse) => {
      if (err.error instanceof Error) {
        console.error('An error occurred:', err.error.message);
      } else {
        console.error(`Backend returned code ${err.status}, body was: ${err.error}`);
      }
    })
  }

  drawTree(expressionTree: ExpressionTree) {
    let treeViewModel = this.treeBuilderService.buildTreeViewModel(expressionTree)

    // create an array with nodes
    var nodes = new vis.DataSet([
      { id: 1, label: "Node 1", leftNode: 1, rightNode: 1 },
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

    // create a network
    var container = document.getElementById("mynetwork");
    var data = {
      nodes: nodes,
      edges: edges
    };
    var options = {
      layout: {
        hierarchical: {
          direction: "UD"
          //direction: "LR"
          //direction: "RL"
        }
      }
    };
    var network = new vis.Network(container, treeViewModel, options);
  }
}
