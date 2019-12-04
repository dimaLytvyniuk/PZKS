import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { Lab1Component } from './components/lab1/lab1.component';
import { BackendClientService } from './services/backend-client.service';
import { HttpClientModule } from '@angular/common/http';
import { Lab2Component } from './components/lab2/lab2.component';
import { Lab3Component } from './components/lab3/lab3.component';
import { TreeBuilderService } from './services/tree-builder.service';
import { Lab4Component } from './components/lab4/lab4.component';
import { Lab5Component } from './components/lab5/lab5.component';

@NgModule({
  declarations: [
    AppComponent,
    Lab1Component,
    Lab2Component,
    Lab3Component,
    Lab4Component,
    Lab5Component
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [
    BackendClientService,
    TreeBuilderService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
