import { Component, OnInit, Input } from '@angular/core';
import { StatisticModel } from '../../models/statistic.model';

@Component({
  selector: 'app-statistic-item',
  templateUrl: './statistic-item.component.html',
  styleUrls: ['./statistic-item.component.css']
})
export class StatisticItemComponent implements OnInit {
  statisticModel: StatisticModel;
  
  @Input() statisticInput: StatisticModel;

  constructor() { }

  ngOnInit() {
    this.statisticModel = this.statisticInput;
  }
}
