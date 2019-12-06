import { Component, OnInit } from '@angular/core';
import { PipelineContainerModel } from 'src/app/models/pipelineContainerModel';
import { BackendClientService } from 'src/app/services/backend-client.service';
import { InputExpression } from 'src/app/models/inputExpression';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-lab6',
  templateUrl: './lab6.component.html',
  styleUrls: ['./lab6.component.css']
})
export class Lab6Component implements OnInit {
  errors: string = null
  pipelineContainers: PipelineContainerModel[];
  inputExpression: string;

  constructor(
    private backEndClient: BackendClientService
  ) { }


  ngOnInit() {
  }

  onChangeExpressionField(event) {
    this.inputExpression = event.target.value;
  }

  onSubmit() {
    this.errors = null
    this.doAnalyze()
  }

  doAnalyze() {
    let inputModel = new InputExpression();
    inputModel.expression = this.inputExpression;

    this.backEndClient.lab6(inputModel).subscribe(data => {
      console.log(data)
      this.pipelineContainers = data
      if (this.pipelineContainers[0].expressionTree.exceptionModel != null)
        this.errors = this.pipelineContainers[0].expressionTree.exceptionModel.message
      else {
        this.pipelineContainers.sort(this.comparePipelineContainer)
        console.log(this.pipelineContainers)
      }
    },
      (err: HttpErrorResponse) => {
        if (err.error instanceof Error) {
          console.error('An error occurred:', err.error.message);
        } else {
          console.error(`Backend returned code ${err.status}, body was: ${err.error}`);
        }
      })
  }

  comparePipelineContainer(first: PipelineContainerModel, second: PipelineContainerModel): number {
    if (first.calculationStatistic.efficiency > second.calculationStatistic.efficiency) {
      return -1;
    } else if (first.calculationStatistic.efficiency < second.calculationStatistic.efficiency) {
      return 1;
    } else {
      0
    }
  }
}
