import { Component, OnInit, Input } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-gant-diagram',
  templateUrl: './gant-diagram.component.html',
  styleUrls: ['./gant-diagram.component.css']
})
export class GantDiagramComponent implements OnInit {
  @Input() tickResults$: Observable<string[][]>;

  constructor() { }

  ngOnInit() {
  }

}
