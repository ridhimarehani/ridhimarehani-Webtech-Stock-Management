import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpService } from '../shared/http.service';

@Component({
  selector: 'app-watchlist',
  templateUrl: './watchlist.component.html',
  styleUrls: ['./watchlist.component.css']
})
export class WatchlistComponent implements OnInit {
  watchlistVal : any = [];
  alertWatchlist = false;
  // compDesc : any = [];
  // latestPrice : any = [];
  countItem: any = 0;
  companyDescription: any;
  stockPrice : any;

  constructor(private httpService: HttpService,private router: Router) { }

  ngOnInit(): void {
    Object.keys(localStorage).forEach(data => 
      {

        let item = localStorage.getItem(data);
        if(data.includes("-Watchlist")){
          this.countItem+=1;
          this.fetchWatchlist(item);
        }
        
      });
      if(this.countItem === 0){
        this.alertWatchlist = true;
      }
  }

  fetchWatchlist(item : any){
    let flag : any;
    let ticker : any;
    let name : any;
    console.log(item);


    this.httpService.getData('companyDescription', item).subscribe(res => {
      this.companyDescription = res;
      ticker = this.companyDescription.ticker;
        name = this.companyDescription.name;

        this.httpService.getData('stockPrice', item).subscribe(res => {
          this.stockPrice = res;
          if(this.stockPrice.dp > 0){
            flag = 1;
          }
          else if(this.stockPrice.dp < 0){
            flag = 2;
            
          }
          else{
            flag = 3;
          }

          this.watchlistVal.push({
            ticker: ticker,
            name: name,
            c : this.stockPrice.c,
            d: this.stockPrice.d,
            dp: this.stockPrice.dp,
            flag : flag
    
          });
        })
    });

    
  }
  loadData(item : any){
    console.log(item);
    this.router.navigate(['/search', item.ticker]);
  }
  cancelButtonClicked(item:any){
    // console.log(item);
    let tickerN;
    // let classnameCard = 'card mb-3 d-flex flex-row col-md-12 ' + item.
    // var parent : any = document.getElementById("main-div");
    // console.log(parent);
    let index : any;
    for(var i = 0;i<this.watchlistVal.length; i++){
      if(item.ticker == this.watchlistVal[i].ticker){
        index = i;
        tickerN = item.ticker;
        break;
      }
    }
    // let content : any = parent?.getElementsByClassName("content");
    // // console.log(content[index])
    
    // if(content[index]){
    //   console.log("yes");
    //   // parent.removeChild(content[index]);

      this.watchlistVal.splice(index, 1);
      localStorage.removeItem(item.ticker + "-Watchlist");
    
      if(this.watchlistVal.length === 0){
        this.alertWatchlist = true;
      }
    }

}
