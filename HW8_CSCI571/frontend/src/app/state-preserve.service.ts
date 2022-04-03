import { Injectable } from '@angular/core';
import { Subject, combineLatest } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class StatePreserveService {
  tickerSymbolSubject: Subject<string> = new Subject<string>();
  tickerSymbol: string = "";
  constructor() {

    this.tickerSymbolSubject.subscribe((value) => {
      this.tickerSymbol = value
    });
  }
  //Lolly

  insertData(data: string) {
    console.log("Indide inset data");
    console.log(data);
    this.tickerSymbolSubject.next(data);

  }
}
