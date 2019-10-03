import { Injectable } from '@angular/core';
import { InputExpression } from '../models/inputExpression';
import { HttpClient } from '@angular/common/http';
import { OutputParsedExpression } from '../models/outputParsedExpression';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BackendClientService {
  private readonly apiBaseUrl: string;
  private readonly lab1Path: string;
  private readonly lab2Path: string;

  constructor(private http: HttpClient) { 
    this.apiBaseUrl = environment['ApiBaseUrl']
    this.lab1Path = this.apiBaseUrl.concat('/expression/lab1')
    this.lab2Path = this.apiBaseUrl.concat('/expression/lab2')
  }

  lab1(inputModel: InputExpression) {
    return this.http.post<OutputParsedExpression>(this.lab1Path, inputModel)
  }

  lab2(inputModel: InputExpression) {
    return this.http.post<OutputParsedExpression>(this.lab2Path, inputModel)
  }
}
