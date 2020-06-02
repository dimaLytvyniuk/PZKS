import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { PlanTasksModel } from '../models/store/plan-tasks.model';
import { StoreNetworkModel } from '../models/store/store-network-model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TaskPlannerApiService {
  private readonly apiBaseUrl: string;
  private readonly lab2Path: string;
  private readonly lab3Path: string;
  private readonly lab6Path: string;
  private readonly rgrPath: string;

  constructor(private http: HttpClient) {
    this.apiBaseUrl = environment['ApiBaseUrl'];
    this.lab2Path = this.apiBaseUrl.concat('/taskPlanner/lab2');
    this.lab3Path = this.apiBaseUrl.concat('/taskPlanner/lab3');
    this.lab6Path = this.apiBaseUrl.concat('/taskPlanner/lab6');
    this.rgrPath = this.apiBaseUrl.concat('/taskPlanner/rgr');
   }

   lab2(graphModel: StoreNetworkModel): Observable<string[]> {
     return this.http.post<any>(this.lab2Path, graphModel);
   }

   lab3(graphModel: StoreNetworkModel): Observable<string[]> {
    return this.http.post<any>(this.lab3Path, graphModel);
  }

   lab6(planTasksModel: PlanTasksModel) {
    return this.http.post<any>(this.lab6Path, planTasksModel);
  }

  rgr(planTasksModels: PlanTasksModel[]) {
    return this.http.post<any>(this.rgrPath, planTasksModels);
  }
}
