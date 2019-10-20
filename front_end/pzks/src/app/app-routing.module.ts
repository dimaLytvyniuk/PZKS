import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { Lab1Component } from './components/lab1/lab1.component';
import { Lab2Component } from './components/lab2/lab2.component';
import { Lab3Component } from './components/lab3/lab3.component';
import { Lab4Component } from './components/lab4/lab4.component';

const routes: Routes = [
  { path: '', component: Lab1Component},
  { path: 'lab1', component: Lab1Component },
  { path: 'lab2', component: Lab2Component },
  { path: 'lab3', component: Lab3Component },
  { path: 'lab4', component: Lab4Component },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
