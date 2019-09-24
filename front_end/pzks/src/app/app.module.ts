import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { Lab1Component } from './components/lab1/lab1.component';
import { BackendClientService } from './services/backend-client.service';

@NgModule({
  declarations: [
    AppComponent,
    Lab1Component
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [
    BackendClientService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }