import { Component, OnInit } from '@angular/core';
import { TaskPlannerApiService } from '../../services/task-planner-api.service';

@Component({
  selector: 'app-rgr-container',
  templateUrl: './rgr-container.component.html',
  styleUrls: ['./rgr-container.component.css']
})
export class RgrContainerComponent implements OnInit {
  
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

    this.apiService.rgr(object).subscribe(data => {
      console.log(data);
    });
  }
}
