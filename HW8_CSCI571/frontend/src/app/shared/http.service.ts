import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'; //R


@Injectable({
  providedIn: 'root'
})
export class HttpService {
  // private baseUrl = 'http://localhost:8080/'; 
  private baseUrl = 'https://csci571hw8-backend-346006.wl.r.appspot.com/';
  urlPath = ''
  constructor(private http: HttpClient) { }

  getData(urlPath: string, ticker: string) {
    
    return this.http.get(`${this.baseUrl}${urlPath}?ticker=${ticker}`);
  }

  getDataHistoric(urlPath: string, ticker: string, fromDate : any, toDate : any) {
    
    return this.http.get(`${this.baseUrl}${urlPath}?ticker=${ticker}&fromDate=${fromDate}&toDate=${toDate}`);
  }

  getDataAutoComplete(urlPath: string, queryVal : string){
    console.log('URL in Service> '+ `${this.baseUrl}${urlPath}?queryVal=${queryVal}`);
    return this.http.get(`${this.baseUrl}${urlPath}?queryVal=${queryVal}`);
  }

  // Error handling
  // private error(error: any) {
  //   let message = (error.message) ? error.message :
  //     error.status ? `${error.status} - ${error.statusText}` : 'Server error';
  //   console.error(message);
  // }
}
