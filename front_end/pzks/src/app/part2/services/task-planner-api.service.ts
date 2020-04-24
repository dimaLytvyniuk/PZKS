import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { PlanTasksModel } from '../models/store/plan-tasks.model';

@Injectable({
  providedIn: 'root'
})
export class TaskPlannerApiService {
  private readonly apiBaseUrl: string;
  private readonly lab2Path: string;

  constructor(private http: HttpClient) {
    this.apiBaseUrl = environment['ApiBaseUrl']
    this.lab2Path = this.apiBaseUrl.concat('/taskPlanner/lab2')
   }

   lab2(planTasksModel: PlanTasksModel) {
     return this.http.post<any>(this.lab2Path, planTasksModel);
   }
}
