import { Component, OnInit } from '@angular/core';
import { OutputParsedExpression } from 'src/app/models/outputParsedExpression';
import { BackendClientService } from 'src/app/services/backend-client.service';
import { TreeBuilderService } from 'src/app/services/tree-builder.service';
import * as vis from 'vis';
import { ExpressionTree } from 'src/app/models/expressionTree';
import { HttpErrorResponse } from '@angular/common/http';
import { InputExpression } from 'src/app/models/inputExpression';

@Component({
  selector: 'app-lab3',
  templateUrl: './lab3.component.html',
  styleUrls: ['./lab3.component.css']
})
export class Lab3Component implements OnInit {
  evaluatedResult: string = null
  errors: string = null
  outputResult: OutputParsedExpression;
  inputExpression: string;

  constructor(
    private backEndClient: BackendClientService,
    private treeBuilderService: TreeBuilderService
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

    this.backEndClient.lab3(inputModel).subscribe(data => {
      console.log(data)
      this.outputResult = data
      if (this.outputResult.exceptionModel != null)
        this.errors = this.outputResult.exceptionModel.message
      if (this.outputResult.evaluatedResult != null)
        this.evaluatedResult = this.outputResult.evaluatedResult

      this.drawTree(data.expressionTree);
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
