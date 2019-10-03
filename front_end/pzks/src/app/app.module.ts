import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { Lab1Component } from './components/lab1/lab1.component';
import { BackendClientService } from './services/backend-client.service';
import { HttpClientModule } from '@angular/common/http';
import { Lab2Component } from './components/lab2/lab2.component';

@NgModule({
  declarations: [
    AppComponent,
    Lab1Component,
    Lab2Component
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [
    BackendClientService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
