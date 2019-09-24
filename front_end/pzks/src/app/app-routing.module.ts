import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { Lab1Component } from './components/lab1/lab1.component';

const routes: Routes = [
  { path: '', component: Lab1Component},
  { path: 'lab1', component: Lab1Component }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
