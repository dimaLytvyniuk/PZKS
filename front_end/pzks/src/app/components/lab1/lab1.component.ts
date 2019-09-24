import { Component, OnInit } from '@angular/core';
import { OutputParsedExpression } from 'src/app/models/outputParsedExpression';
import { InputExpression } from 'src/app/models/inputExpression';
import { BackendClientService } from 'src/app/services/backend-client.service';
import { HttpErrorResponse } from '@angular/common/http/http';

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

  constructor(private backEndClient: BackendClientService) { }

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
}
