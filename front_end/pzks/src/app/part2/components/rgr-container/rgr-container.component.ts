import { Component, OnInit } from '@angular/core';
import { TaskPlannerApiService } from '../../services/task-planner-api.service';
import { Observable } from 'rxjs';
import { StatisticModel } from '../../models/statistic.model';
import { CombinedStatisticModel } from '../../models/combined-statistic.model';

@Component({
  selector: 'app-rgr-container',
  templateUrl: './rgr-container.component.html',
  styleUrls: ['./rgr-container.component.css']
})
export class RgrContainerComponent implements OnInit {
  statisticResults$: Observable<CombinedStatisticModel>;

  constructor(private apiService: TaskPlannerApiService) { }

  ngOnInit() {
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
    const fileData = $event.target.result;
    const object = JSON.parse(fileData);

    this.statisticResults$ = this.apiService.rgr(object);
  }
}
