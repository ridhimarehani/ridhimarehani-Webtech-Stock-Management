import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpService } from '../shared/http.service';
import { Router } from '@angular/router';
import { Subject, Observable } from 'rxjs';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { switchMap, debounceTime, tap, finalize, map, startWith } from 'rxjs/operators';
import { StatePreserveService } from '../state-preserve.service';


@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css']
})
export class SearchBarComponent implements OnInit {
  autoSuggestions!: any;
  autoSuggestionsArray: any
  autoCompleteForm!: FormGroup;
  isLoading = false;
  public tickerSymbol: string = '';

  changingValue: Subject<string> = new Subject();
  searchButtonClicked = false;

  tellChild() {
    console.log('this.tickerSymbol> ' + this.tickerSymbol);
    this.changingValue.next(this.tickerSymbol);
  }

  // title = 'frontend';
  // private route1Path = 'route1'; //R

  constructor(private router: Router, private service: HttpService, private formBuilder: FormBuilder, private stateServie: StatePreserveService) { }
  ngOnInit() {
    this.getValueOnSearch();
    this.loadAutoComplete();
  }

  getValueOnSearch() {
    let activeUrl: any = window.location.href;
    let tickerValue = activeUrl.split('/');
    if (tickerValue[4] !== 'home' && tickerValue[4]) {
      this.searchButtonClicked = true;
      this.tickerSymbol = tickerValue[4];
      this.changingValue.next(this.tickerSymbol);
      this.router.navigate(['/search', this.tickerSymbol]);
      this.stateServie.insertData(this.tickerSymbol);
    }
  }

  loadAutoComplete() {
    let typedVal = '';
    this.autoCompleteForm = this.formBuilder.group({ tickerInput: '' });
    if (this.autoCompleteForm.get('tickerInput') != null) {
      this.autoCompleteForm.get('tickerInput')?.valueChanges.pipe(
        debounceTime(300),
        tap(() => (this.isLoading = true)),
        switchMap((value) => {
          console.log(' switchMap VAL> ', value);
          // console.log('URL HIT> '+ JSON.stringify(this.service.getDataAutoComplete('autoComplete', value)));
          typedVal = value;
          return this.service.getDataAutoComplete('autoComplete', value)
            // .pipe(finalize(() => (this.isLoading = false)));
        }
        )
      ).subscribe(res => {
        this.autoSuggestions = res;
        this.autoSuggestionsArray = [];
        if (typedVal === undefined || typedVal === '') {
          this.autoSuggestionsArray = [];
        }
        else {
          for (let data of this.autoSuggestions.result) {
            if (data.type === "Common Stock" && data.symbol.includes(typedVal)
              && !data.displaySymbol.includes(".") && !data.symbol.includes(".")) {
              this.autoSuggestionsArray.push(data);
            }
          }
          this.isLoading = false;
        }
      });
    }

  }

  loadStockDetails(tickerData: any) {
    console.log('ticketData: ', tickerData);
    this.autoSuggestionsArray = [];
    if (tickerData.displaySymbol) {
      this.tickerSymbol = tickerData.displaySymbol;
    }
    this.searchButtonClicked = true;
    this.router.navigate(['/search', this.tickerSymbol], {});
    this.tellChild();
    this.stateServie.insertData(this.tickerSymbol);
    // this.autoCompleteForm.reset();
  }

  clearSearchDetails() {
    this.isLoading = false;
    this.searchButtonClicked = false;
    this.tickerSymbol = "";
    this.router.navigate(['/search/home'], {});
  }

  // displayFn(company: any) {
  //   if (company) {
  //     return company.result.displaySymbol;
  //   }
  //   return '';
  // }

}
