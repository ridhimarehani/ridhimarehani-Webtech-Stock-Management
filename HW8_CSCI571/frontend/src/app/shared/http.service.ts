import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'; //R


@Injectable({
  providedIn: 'root'
})
export class HttpService {
  private baseUrl = 'http://localhost:8080/'; //R
  urlPath = ''
  constructor(private http: HttpClient) { }

  getData(urlPath: string, ticker: string) {
    console.log('yayy> '+`${this.baseUrl}${urlPath}?ticker=${ticker}`);
    return this.http.get(`${this.baseUrl}${urlPath}?ticker=${ticker}`);
  }

  getDataHistoric(urlPath: string, ticker: string, fromDate : any, toDate : any) {
    console.log('yayy> '+`${this.baseUrl}${urlPath}?ticker=${ticker}&fromDate=${fromDate}&toDate=${toDate}`);
    return this.http.get(`${this.baseUrl}${urlPath}?ticker=${ticker}&fromDate=${fromDate}&toDate=${toDate}`);
  }

  // Error handling
  // private error(error: any) {
  //   let message = (error.message) ? error.message :
  //     error.status ? `${error.status} - ${error.statusText}` : 'Server error';
  //   console.error(message);
  // }
}
