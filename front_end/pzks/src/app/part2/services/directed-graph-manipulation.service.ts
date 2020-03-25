import { BaseGraphManipulationService } from './base-graph-manipulation.service';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class DirectedGraphManipulationService extends BaseGraphManipulationService {
    constructor() { 
        super();
    }
}