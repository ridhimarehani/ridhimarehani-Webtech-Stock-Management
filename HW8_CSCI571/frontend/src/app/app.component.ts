import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { getLocaleDateFormat } from '@angular/common';
import { HttpService } from './shared/http.service';
import { SearchBarComponent } from './search-bar/search-bar.component';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  constructor() { }
  ngOnInit() {
    // console.log('res in app.comp> '+ SearchBarComponent.companyDescription)
  }
}


