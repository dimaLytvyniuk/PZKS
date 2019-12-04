import { Component, OnInit } from '@angular/core';
import * as vis from 'vis';
import { OutputParsedExpression } from 'src/app/models/outputParsedExpression';
import { PipelineContainerModel } from 'src/app/models/pipelineContainerModel';
import { TreeBuilderService } from 'src/app/services/tree-builder.service';
import { BackendClientService } from 'src/app/services/backend-client.service';
import { ExpressionTree } from 'src/app/models/expressionTree';
import { HttpErrorResponse } from '@angular/common/http';
import { InputExpression } from 'src/app/models/inputExpression';

@Component({
  selector: 'app-lab5',
  templateUrl: './lab5.component.html',
  styleUrls: ['./lab5.component.css']
})
export class Lab5Component implements OnInit {
  evaluatedResult: string = null
  errors: string = null
  pipelineContainer: PipelineContainerModel;
  outputResult: OutputParsedExpression;
  inputExpression: string;
  
  constructor(
    private backEndClient: BackendClientService,
    private treeBuilderService: TreeBuilderService,
  ) { }

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

    this.backEndClient.lab5(inputModel).subscribe(data => {
      console.log(data)
      this.pipelineContainer = data
      this.outputResult = data.expressionTree
      if (this.outputResult.exceptionModel != null)
        this.errors = this.outputResult.exceptionModel.message
      if (this.outputResult.evaluatedResult != null)
        this.evaluatedResult = this.outputResult.evaluatedResult

      this.drawTree(this.outputResult.expressionTree);
      console.log(this.evaluatedResult);
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

    // create a network
    var container = document.getElementById("mynetwork");
    var options = {
      layout: {
        hierarchical: {
          direction: "UD"
        }
      }
    };
    var network = new vis.Network(container, treeViewModel, options);
  }
}
