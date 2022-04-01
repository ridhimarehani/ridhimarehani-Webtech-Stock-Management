import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpService } from '../shared/http.service';
import { Router } from '@angular/router';
import { SearchDetailsComponent } from './search-details/search-details.component';
import { Subject } from 'rxjs';



@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css']
})
export class SearchBarComponent implements OnInit {
  changingValue: Subject<string> = new Subject();
  searchButtonClicked = false;
  tellChild() {
    this.changingValue.next(this.tickerSymbol);
  }

  title = 'frontend';
  // private route1Path = 'route1'; //R
  public tickerSymbol: string = '';
  constructor(private router: Router) { }
  ngOnInit() {
    //Lolly
    let curr_url : any = window.location.href;
      let tickerVal = curr_url.split('/');
      // console.log(tickerVal[4]);
      if(tickerVal[4] && tickerVal[4] !== 'home'){
        this.tickerSymbol = tickerVal[4];
        // console.log(this.searchFlag);
        this.searchButtonClicked = true;
        this.changingValue.next(this.tickerSymbol);
        this.router.navigate(['/search', this.tickerSymbol]);
        
      }
      //Lolly
      
  }
  loadStockDetails() {
    console.log('inside loadStockDetails');
    this.searchButtonClicked = true;
    this.router.navigate(['/search', this.tickerSymbol], {});
    this.tellChild();

  }
  //Lolly
  cancelButtonClicked(){
    // this.isLoading = false;
    this.searchButtonClicked = false;
    this.tickerSymbol ="";
    this.router.navigate(['/search/home'], {});
  }

}
