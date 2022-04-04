import { Component, Input, OnInit, Output, EventEmitter, OnChanges } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpService } from 'src/app/shared/http.service';
import { Subject, timer } from 'rxjs';
import { MatTabsModule } from '@angular/material/tabs';
import { HighchartsChartModule } from 'highcharts-angular';
import * as Highcharts from 'highcharts';
import { NgbModal, ModalDismissReasons, NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { faTwitter, faFacebookSquare } from '@fortawesome/free-brands-svg-icons';
import { debounceTime, combineLatest } from 'rxjs/operators';
//Lolly
import { Router } from '@angular/router';
import IndicatorCore from 'highcharts/indicators/indicators';
import vbp from 'highcharts/indicators/volume-by-price';
import StockModule from 'highcharts/modules/stock';
import HStockModule from 'highcharts/modules/stock-tools';
StockModule(Highcharts);
HStockModule(Highcharts);
IndicatorCore(Highcharts);
vbp(Highcharts);
//Lolly

// import { ViewChild } from '@angular/core';
// import { ModalNewsComponent } from '../modal-news/modal-news.component';
// import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
// import { ActivatedRoute } from "@angular/router";
// import { switchMap } from "rxjs/operators"


@Component({
  selector: 'app-search-details',
  templateUrl: './search-details.component.html',
  styleUrls: ['./search-details.component.css']
})
export class SearchDetailsComponent implements OnInit {
  faTwitter = faTwitter;
  faFacebook = faFacebookSquare;

  // Lolly
  Highcharts: typeof Highcharts = Highcharts;
  chartOptions: Highcharts.Options = {};
  chartOptionsHistorical: Highcharts.Options = {};
  chartOptionsMain: Highcharts.Options = {};
  chartOptionsSummary: Highcharts.Options = {};
  wishlist_add = false;
  watchlistAddMessage = '';
  watchlistRemoveMessage = '';
  wishlist_remove = false;
  display_filled_star = false;
  display_star = false;
  addMessage = '';
  removeMessage = '';
  buyMessage = '';
  sellMessage = '';
  invalidTicker = false;
  enteredQuantity: number = 0;
  enteredQuantitySell: number = 0;
  totalPrice: string = "0.00";
  Current_Price: any;
  buy_button = false;
  sell_button = false;
  fetchSubscribe: any;
  // latestStockPrice = false;

  //lolly


  // @ViewChild(ModalNewsComponent) newsModal : any;

  @Input() changing !: Subject<string>;
  @Input('tickerSymbol') tickerSymbol = '';
  @Output() getResponse = new EventEmitter;
  private route1Path: any = [];
  public companyDescription: any = [];
  public historicalData: any = [];
  public stockPrice: any = [];
  public autoComplete: any = [];
  public companyNews: any = [];
  public recommendation: any = [];
  public socialSentiment: any = [];
  public companyPeers: any = [];
  public companyEarnings: any = [];
  public totalMentionRed = 0;
  public positiveMentionRed = 0;
  public negativeMentionRed = 0;
  public totalMentionTwit = 0;
  public positiveMentionTwit = 0;
  public negativeMentionTwit = 0;
  private periodForRecomm: any = [];
  private dataForRecomm: any = [];
  private epsSurpriseDataX: any = [];
  private epsSurpriseDataY: any = [];
  public newsData20: any = [];
  public newLenCard = 10;
  public historicDataSummary: any;
  isChartLoaded = false;
  isChartLoadedHistorical = false;
  isNewsLoaded = false;
  public closeResult: string = '';
  public currentNews: any;
  public historicalCharts: any;
  moneyInWallet: any;
  marketMessage = 'Market is Closed';

  //Lolly
  public showMainChart = false;
  change_percent = false;
  market_open = false;
  curr_time: any;
  date_today_6: any;
  unix_date: any;
  unix_date_6: any;
  curr_date: any;
  timestamp: any;
  showChart = false;
  private _watchlistAdd = new Subject<string>();
  private _watchlistRemove = new Subject<string>();
  private _buySuccess = new Subject<string>();
  private _sellSuccess = new Subject<string>();
  closeModal = '';
  stockBuy = false;
  stockLeft: any;
  spinner = false;
  //Lolly


  constructor(private http: HttpClient, private httpService: HttpService, private modalService: NgbModal, private router: Router) { }
  open(content: any, newsData: any) {

    this.modalService.open(content, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
    this.currentNews = newsData;
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  ngOnInit(): void {
    console.log("ngOnInit");
    // localStorage.clear();
    this.tickerSymbol = this.tickerSymbol.toUpperCase();
    this.changing.subscribe(v => {
      console.log('value is changing', v);
      this.tickerSymbol = v.toUpperCase();
      this.fetchAPI();
      this.getWatchlist();
      this.enableSellButton();
    });
    this.fetchAPI();
    this.getWatchlist();

    this.alertMessageDisplay();
    this.setWalletMoney();
    this.enableSellButton();
  }

  setWalletMoney(){
    this.moneyInWallet = localStorage.getItem('moneyInWallet')//parseFloat(money);
    if (!this.moneyInWallet) {
      localStorage.setItem('moneyInWallet', "25000.00")
    }
  }

  alertMessageDisplay() {
    //Lolly
    this._buySuccess.subscribe(
      (message) => (this.buyMessage = message)
    );
    this._buySuccess
      .pipe(debounceTime(5000))
      .subscribe(() => (this.buyMessage = ''));

    this._sellSuccess.subscribe(
      (message) => (this.sellMessage = message)
    );
    this._sellSuccess
      .pipe(debounceTime(5000))
      .subscribe(() => (this.sellMessage = ''));

    this._watchlistAdd.subscribe(
      (message) => (this.addMessage = message)
    );
    this._watchlistAdd
      .pipe(debounceTime(5000))
      .subscribe(() => (this.addMessage = ''));

    this._watchlistRemove.subscribe(
      (message) => (this.removeMessage = message)
    );
    this._watchlistRemove
      .pipe(debounceTime(5000))
      .subscribe(() => (this.removeMessage = ''));

    let money: any = localStorage.getItem('moneyInWallet')
    this.moneyInWallet = localStorage.getItem('moneyInWallet')//parseFloat(money);
    //Lolly
  }

  fetchAPICombined(){
    let companyDescription = this.httpService.getData('companyDescription', this.tickerSymbol);
    let latestStockPrice = this.httpService.getData('stockPrice', this.tickerSymbol);
    let companyPeers = this.httpService.getData('companyPeers', this.tickerSymbol);

    this.timeManipulation();
    let historicData = this.httpService.getDataHistoric('historicalDataSummary', this.tickerSymbol, this.unix_date_6, this.unix_date);

    let newsData = this.httpService.getData('companyNews', this.tickerSymbol);
    let companyRecomm = this.httpService.getData('recommendation', this.tickerSymbol);
    let comanySocial = this.httpService.getData('socialSentiment', this.tickerSymbol);
    let earnings = this.httpService.getData('companyEarnings', this.tickerSymbol);
    this.spinner = true;

  
    // combineLatest(comp_desc, latest_stock_p, comp_peer, hist_chart, news_data, comp_recomm, comp_social, comp_earn).subscribe(([res1, res2, res3, res4, res5, res6, res7, res8]: [any, any, any, any, any, any, any, any]) => {
    //   this.companyDescription = res1;
    //   // this.display_star = true;
    //   this.buy_button = true;

    //   this.latestStockPrice = res2;
    //   this.Current_Price = this.latestStockPrice.c;

    //   this.timeManipulation();
    //   this.statusService.getHistoricalSummary(this.inputEnteredTicker, this.unix_date_6, this.unix_date).subscribe(res => {
    //     this.historicalSummary = res;
    //     console.log(this.historicalSummary);
    //     this.historySummaryCharts();
    //   });

    //   this.companyPeers = res3;

    //   this.historicalCharts = res4;
    //   this.historyMainCharts();

    //   this.companyNews = res5;
    //   this.loadNews();
    //   this.isNews = true;

    //   this.companyRecommendation = res6;
    //   this.recommendationData()
    //   this.isRecommChartLoaded = true
    //   this.recommendationCharts()

    //   this.companySocialSentiments = res7;
    //   this.addSociaSentiments();

    //   this.companyEarnings = res8;
    //   this.historicalEPSData();
    //   this.isHistoricalEpsChart = true;
    //   this.historicalEpsChart();)

  }

  fetchAPI() {
    console.log('fetchAPI method starts');
    this.spinner = true;

    this.httpService.getData('companyDescription', this.tickerSymbol).subscribe(res => {
      this.companyDescription = res;
      console.log('companyDescription> ' + JSON.stringify(res));
      this.spinner = false;
      if (Object.keys(this.companyDescription).length === 0) {
        this.invalidTicker = true;
      }
      else {
        this.invalidTicker = false;
        this.buy_button = true;
      }

    });
    if (!this.invalidTicker) {
      this.httpService.getData('historicalData', this.tickerSymbol).subscribe(res => {
        // console.log('historicalData> ' + JSON.stringify(res));
        this.historicalCharts = res;
        this.showMainChart = true;
        console.log('companyDescription> ');
        this.historyMainCharts();


      });



      // this.httpService.getData('stockPrice', this.tickerSymbol).subscribe(res => console.log(JSON.stringify(res)));
      this.httpService.getData('stockPrice', this.tickerSymbol).subscribe(res => {
        this.stockPrice = res;
        this.Current_Price = this.stockPrice.c;


        //Lolly
        this.timeManipulation();
        console.log('unixDatess>> ' + this.unix_date_6 + '  ' + this.unix_date);
        this.httpService.getDataHistoric('historicalDataSummary', this.tickerSymbol, this.unix_date_6, this.unix_date).subscribe(res => {

          this.historicDataSummary = res;
          this.historySummaryCharts();
          this.showChart = true;

        });
        //Lolly
      });
      // this.httpService.getData('autoComplete', this.tickerSymbol).subscribe(res => { this.autoComplete = res; console.log('bbb') });
      this.httpService.getData('companyNews', this.tickerSymbol).subscribe(res => {
        this.companyNews = res;
        this.loadDataForNews(this.companyNews);
        this.isNewsLoaded = true;

      });

      this.httpService.getData('recommendation', this.tickerSymbol).subscribe(res => {
        this.recommendation = res;
        this.loadDataForRecomChart(this.recommendation);
        this.isChartLoaded = true;
        this.chartForRecommendation();

      });
      this.httpService.getData('socialSentiment', this.tickerSymbol).subscribe(res => {
        this.socialSentiment = res;
        this.loadValuesForSocial(this.socialSentiment)

      });
      this.httpService.getData('companyPeers', this.tickerSymbol).subscribe(res => {
        this.companyPeers = res;

      });

      this.httpService.getData('companyEarnings', this.tickerSymbol).subscribe(res => {

        this.companyEarnings = res;
        this.loadDataForHistoricalEPS(this.companyEarnings);
        this.isChartLoadedHistorical = true;
        this.chartForHistorical();

      });

    }

    this.fetchSubscribe = timer(0, 15000).subscribe(() => {
      this.httpService.getData('companyDescription', this.tickerSymbol).subscribe(res => {
        this.companyDescription = res;

      });
      // this.display_star = true;
      // this.buy_button = true;
      this.httpService.getData('stockPrice', this.tickerSymbol).subscribe(res => {
        this.stockPrice = res;
        this.Current_Price = this.stockPrice.c;
        //Lolly
        this.timeManipulation();
        this.httpService.getDataHistoric('historicalDataSummary', this.tickerSymbol, this.unix_date_6, this.unix_date).subscribe(res => {

          this.historicDataSummary = res;
          this.historySummaryCharts();
          this.showChart = true;

        });
        //Lolly
      });
    });

  }

  buyStock() {
    this._buySuccess.next('Message successfully changed.');
    // this.portfolio_add = true;
    // this.portfolio_remove = false;
    if (this.enteredQuantity > 0) {
      let total = this.Current_Price * this.enteredQuantity;
      console.log(total);
      let val: any = localStorage.getItem('moneyInWallet')
      let wallet: any = parseFloat(val);
      wallet = wallet - total;
      console.log(wallet);
      localStorage.setItem('moneyInWallet', wallet.toFixed(2).toString());
      // console.log(wallet);
      // if((localStorage.getItem(this.inputEnteredTicker+"-Portfolio"))){
      // {"ticker" : "TSLA", "qty" : 4, "amount" : am1};
      if (localStorage.getItem(this.tickerSymbol + "-Portfolio")) {
        let stockValJson: any = localStorage.getItem(this.tickerSymbol + "-Portfolio");
        let stockVal = JSON.parse(stockValJson);
        let quantity = this.enteredQuantity + stockVal.quantity;
        total = total + stockVal.totalPrice;
        localStorage.setItem(this.tickerSymbol + "-Portfolio", JSON.stringify({ "ticker": this.tickerSymbol, "qty": quantity, "amount": total }))

      }
      else {
        localStorage.setItem(this.tickerSymbol + "-Portfolio", JSON.stringify({ "ticker": this.tickerSymbol, "qty": this.enteredQuantity, "amount": total }))
      }

      // console.log(localStorage.getItem(this.inputEnteredTicker+"-Portfolio"));
      let stockValJson: any = localStorage.getItem(this.tickerSymbol + "-Portfolio");
      let stockVal = JSON.parse(stockValJson);
      console.log('stockVal>' + stockVal);
      if (stockVal.qty > 0) {
        this.sell_button = true;
        console.log(this.sell_button)
      }

      // this.modal.close();
    }

    this.modalService.dismissAll();


  }

  sellStock() {
    this._sellSuccess.next('Message successfully changed.');
    // this.portfolio_remove = true;
    // this.portfolio_add = false;
    if (this.enteredQuantitySell > 0) {
      let total = this.Current_Price * this.enteredQuantitySell;
      console.log(total);
      let val: any = localStorage.getItem('moneyInWallet')
      let wallet: any = parseFloat(val);
      wallet = wallet + total;
      localStorage.setItem('moneyInWallet', wallet.toFixed(2).toString());
      let stockValJson: any = localStorage.getItem(this.tickerSymbol + "-Portfolio");
      let stockVal = JSON.parse(stockValJson);
      let stockQ = stockVal.qty - this.enteredQuantitySell
        ;
      total = stockVal.totalPrice - total;
      localStorage.setItem(this.tickerSymbol + "-Portfolio", JSON.stringify({ "ticker": this.tickerSymbol, "qty": stockQ, "amount": total }))
      if (stockQ > 0) {
        this.sell_button = true;
        // console.log(this.sell_button)
      }
      else {
        this.sell_button = false;
        localStorage.removeItem(this.tickerSymbol + "-Portfolio")
      }
    }
    this.modalService.dismissAll();
  }
  enableSellButton() {
    if (localStorage.getItem(this.tickerSymbol + "-Portfolio")) {
      let stockValJson: any = localStorage.getItem(this.tickerSymbol + "-Portfolio");
      let stockVal = JSON.parse(stockValJson);
      let stockQ = stockVal.qty;
      if (stockQ > 0) {
        this.sell_button = true;
      }
      else {
        this.sell_button = false;
        // localStorage.removeItem(this.inputEnteredTicker+"-Portfolio")
      }
    }

  }

  //Lolly
  peerCall(peer: any) {
    // console.log(peer);

    this.router.navigate(['/search', peer]);
    this.changing.next(peer);
    this.tickerSymbol = peer;
    // this.getWatchlist();
    this.fetchAPI();
  }

  timeManipulation() {

    this.curr_date = new Date();
    this.curr_time = new Date(this.stockPrice.t * 1000);
    this.timestamp = this.curr_time.getFullYear() + "-" + (this.curr_time.getMonth() + 1 < 10 ? "0" : "") + (this.curr_time.getMonth() + 1) +
      "-" + (this.curr_time.getDate() < 10 ? "0" : "") + this.curr_time.getDate() + " " +
      (this.curr_time.getHours() < 10 ? "0" : "") + this.curr_time.getHours() + ":" + (this.curr_time.getMinutes() < 10 ? "0" : "")
      + this.curr_time.getMinutes() + ":" + (this.curr_time.getSeconds() < 10 ? "0" : "") + this.curr_time.getSeconds();
    // if (this.companyDescription && this.companyDescription.ticker) {
    //   this.display_star = true;

    // }
    if (this.stockPrice.dp >= 0) {
      this.change_percent = true;
    }

    this.market_open = this.curr_time > (this.curr_date - (5 * 60 * 1000));

    if (this.market_open) {
      this.marketMessage = 'Market is Open';
      this.unix_date = Math.floor(Date.now() / 1000)
      this.date_today_6 = this.curr_date.setHours(this.curr_date.getHours() - 6)
      this.unix_date_6 = Math.floor(this.date_today_6 / 1000)

    }
    else {
      this.unix_date = this.stockPrice.t
      this.date_today_6 = this.curr_time.setHours(this.curr_time.getHours() - 6)
      this.unix_date_6 = Math.floor(this.date_today_6 / 1000);

    }
  }

  //Lolly



  loadValuesForSocial(socialSent: any) {
    let redditVals = socialSent.reddit;
    let twitterVal = socialSent.twitter;
    for (let i = 0; i < redditVals.length; i++) {
      this.totalMentionRed += redditVals[i].mention;
      this.positiveMentionRed += redditVals[i].positiveMention;
      this.negativeMentionRed += redditVals[i].negativeMention;
    }
    for (let i = 0; i < twitterVal.length; i++) {
      this.totalMentionTwit += twitterVal[i].mention;
      this.positiveMentionTwit += twitterVal[i].positiveMention;
      this.negativeMentionTwit += twitterVal[i].negativeMention;
    }

  }

  loadDataForNews(newsVal: any) {
    this.newsData20 = [];
    let newsLen = 20;
    let tempNewsData = [];
    if (this.companyNews < 20) {
      newsLen = this.companyNews.length
    }

    let k = 0;
    for (let item of newsVal) {
      if (item.image != '' && item.image != undefined
        && item.headline != '' && item.headline != undefined
        && item.url != '' && item.url != undefined) {

        // let tempDate = this.convertDateFromUnix(item.datetime);
        item.datetime = this.convertDateFromUnix(item.datetime);
        tempNewsData.push(item);
        k += 1;
      }
      if (k == newsLen) {
        break;
      }
    }

    let finalNewsLen = tempNewsData.length
    let j = 0;

    while (j < (finalNewsLen - 1)) {
      let tempList = [];
      tempList.push(tempNewsData[j]);
      tempList.push(tempNewsData[j + 1]);
      this.newsData20.push(tempList);
      j += 2;
    }
    let tempList1 = []
    if (j < finalNewsLen) {
      tempList1.push(tempNewsData[finalNewsLen - 1]);
      tempList1.push([]);
      this.newsData20.push(tempList1);
    }

  }

  convertDateFromUnix(unixDate: any) {
    //Februray 24,2023
    let date = new Date(unixDate * 1000);
    let months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
    // console.log('DateFormat> '+months[date.getMonth()]+' '+date.getDay()+','+date.getFullYear());
    let reqDate = months[date.getMonth()] + ' ' + date.getDay() + ',' + date.getFullYear()
    return reqDate;
  }

  loadDataForRecomChart(dataRecom: any) {
    let strongBuy = [];
    let buy = [];
    let hold = [];
    let sell = [];
    let strongSell = [];
    console.log('dataRecom>> ' + JSON.stringify(dataRecom));
    for (let i = 0; i < dataRecom.length; i++) {
      let tempStr = new String();
      tempStr = dataRecom[i].period;
      this.periodForRecomm.push(tempStr.substring(0, 7));
      strongBuy.push(dataRecom[i].strongBuy);
      buy.push(dataRecom[i].buy);
      hold.push(dataRecom[i].hold);
      sell.push(dataRecom[i].sell);
      strongSell.push(dataRecom[i].strongSell);
    }
    this.dataForRecomm = [strongBuy, buy, hold, sell, strongSell];
    // this.dataForRecomm.push(strongBuy);
    // this.dataForRecomm.push(buy);

  }

  //Lolly
  openPortfolioBuy(portfolioModalBuy: any, stockDeal: any) {
    if (stockDeal === 'buy') {
      this.stockBuy = true;
    }
    else {
      this.stockBuy = false;
    }
    let money: any = localStorage.getItem('moneyInWallet')
    this.moneyInWallet = parseFloat(money);

    this.modalService.open(portfolioModalBuy, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {
      this.closeModal = `Closed with: ${result}`;
    }, (reason) => {
      this.closeModal = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }
  openPortfolioSell(portfolioModalSell: any, stockDeal: any) {
    if (stockDeal === 'buy') {
      this.stockBuy = true;
    }
    else {
      this.stockBuy = false;
    }

    let money: any = localStorage.getItem('moneyInWallet')
    this.moneyInWallet = parseFloat(money);
    let stockValJson: any = localStorage.getItem(this.tickerSymbol + "-Portfolio");
    let stockVal = JSON.parse(stockValJson);

    this.stockLeft = stockVal.qty;

    this.modalService.open(portfolioModalSell, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {
      this.closeModal = `Closed with: ${result}`;
    }, (reason) => {
      this.closeModal = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  //Lolly 

  chartForRecommendation() {
    //TBD: legend in 1line, remove total , date in straight way
    console.log('recom pe>' + this.periodForRecomm);
    this.chartOptions = {
      chart: { type: 'column' },
      title: {
        text: 'Recommendation Trends',
        style: {
          fontSize: '15px',
          color: '#29363E'
        }
      },
      xAxis: { categories: this.periodForRecomm },
      yAxis: {
        min: 0,
        title: { text: '#Analysis', align: 'high' },
        stackLabels: {
          enabled: true,
          style: {
            fontWeight: 'bold',
            color: ( // theme
              Highcharts.defaultOptions.title?.style &&
              Highcharts.defaultOptions.title.style.color
            ) || 'gray'
          }
        },
      },
      legend: {
        align: 'center',
        x: -24,
        // verticalAlign: 'bottom',
        y: 0,
        // floating: true,
        backgroundColor: Highcharts.defaultOptions.legend?.backgroundColor || 'white',
        // borderColor: '#CCC',
        // borderWidth: 1,
        shadow: false,
        itemStyle: {
          fontSize: '7px',
        }
      },
      plotOptions: {
        column: {
          stacking: 'normal',
          dataLabels: {
            enabled: true
          }
        }
      },
      series: [{
        // groupPadding: 0,
        // pointPadding: 0.1,
        name: 'Strong Buy',
        type: 'column',
        data: this.dataForRecomm[0],
        color: '#186F37',
        pointWidth: 38
      }, {
        name: 'Buy',
        type: 'column',
        data: this.dataForRecomm[1],
        color: '#1CB955',
        pointWidth: 38
      }, {
        name: 'Hold',
        type: 'column',
        data: this.dataForRecomm[2],
        color: '#B98B1D',
        pointWidth: 38
      }, {
        name: 'Sell',
        type: 'column',
        data: this.dataForRecomm[3],
        color: 'rgb(255,0,0)',
        pointWidth: 38
      }, {
        name: 'Strong Sell',
        type: 'column',
        data: this.dataForRecomm[4],
        color: '#803131',
        pointWidth: 38
      }]
    };

  }

  loadDataForHistoricalEPS(dataHistoric: any) {
    // epsSurpriseDataX
    this.epsSurpriseDataX = [];
    console.log('dataHistoric> ' + JSON.stringify(dataHistoric));
    let actualData = [];
    let estimateData = [];
    // for (let i = 0; i < dataHistoric.length; i++) {
    //   this.epsSurpriseDataX.push(`"${dataHistoric[i].period} Surprise: ${dataHistoric[i].surprise}"`);
    //   actualData.push(dataHistoric[i].actual);
    //   estimateData.push(dataHistoric[i].estimate);
    // }

    for (let i = 0; i < dataHistoric.length; i++) {
      let list1 = [dataHistoric[i].period + ' Surprise:' + dataHistoric[i].surprise, dataHistoric[i].actual]
      let list2 = [dataHistoric[i].period + ' Surprise:' + dataHistoric[i].surprise, dataHistoric[i].estimate]
      this.epsSurpriseDataX.push(dataHistoric[i].period + ' Surprise:' + dataHistoric[i].surprise);
      // actualData.push(this.companyEarnings[i].actual);
      actualData.push(list1);
      estimateData.push(list2);
      // estimateData.push(this.companyEarnings[i].estimate);
    }



    console.log('this.epsSurpriseDataX> ' + this.epsSurpriseDataX);

    this.epsSurpriseDataY = [actualData, estimateData];
    // console.log('this.epsSurpriseDataY> '+this.epsSurpriseDataY);
  }

  chartForHistorical() {
    this.chartOptionsHistorical = {
      title: {
        text: 'Historical EPS Surprises',
        style: {
          fontSize: '15px',
          color: '#29363E'
        }
      },

      yAxis: {
        title: {
          text: 'Quarterly EPS'
        }
      },

      // xAxis: {
      //   accessibility: {
      //     rangeDescription: this.epsSurpriseDataX
      //   }
      // },

      xAxis: {
        type: 'category',
        categories: this.epsSurpriseDataX,

        labels: {
          rotation: 0,
          useHTML: true,
          allowOverlap: true,
          style: {
            // width: '10px',
            fontSize: '10px',
            wordBreak: 'break-all',
            textAlign: 'center',
            textOverflow: 'allow',
          }
        }

      },

      // xAxis: { 
      //   type: 'category',
      //   categories: this.epsSurpriseDataX },

      legend: {
        layout: 'vertical',
        align: 'right',
        verticalAlign: 'middle'
      },

      plotOptions: {
        series: {
          label: {
            connectorAllowed: false
          },
          pointStart: 2010
        }
      },

      series: [{
        name: 'Actual',
        type: 'line',
        data: this.epsSurpriseDataY[0]
      }, {
        name: 'Estimate',
        type: 'line',
        data: this.epsSurpriseDataY[1]
      }],

      responsive: {
        rules: [{
          condition: {
            maxWidth: 500
          },
          chartOptions: {
            legend: {
              layout: 'horizontal',
              align: 'center',
              verticalAlign: 'bottom'
            }
          }
        }]
      }
    }

  }


  //Lolly
  historyMainCharts() {
    // split the data set into ohlc and volume
    // console.log(this.historicalCharts);
    let ohlc = [], volume = [];


    // set the allowed units for data grouping
    let groupingUnits: any = [[
      'week',                         // unit name
      [1]                             // allowed multiples
    ], [
      'month',
      [1, 2, 3, 4, 6]
    ]];

    for (var i = 0; i < this.historicalCharts['c'].length; i++) {

      ohlc[i] = [
        (this.historicalCharts['t'][i] * 1000),
        this.historicalCharts['o'][i],
        this.historicalCharts['h'][i],
        this.historicalCharts['l'][i],
        this.historicalCharts['c'][i]
      ];

      volume[i] = [
        (this.historicalCharts['t'][i] * 1000),
        this.historicalCharts['v'][i]
        // data[i][0], // the date
        // data[i][5] // the volume
      ];
    }
    // console.log(ohlc);
    // console.log("vol",volume)
    Highcharts.setOptions({
      time: {
        timezoneOffset: 7 * 60
      }
    });
    this.chartOptionsMain = {


      // xAxis:[{
      //   scrollbar: {
      //     enabled: true
      // },
      // type: 'datetime',
      title: {
        text: this.tickerSymbol + " Historical",
        style: {
          // color: '#9e9e9f',
          fontSize: '15',
        }
      },
      legend: { enabled: false },
      subtitle: {
        text: 'With SMA and Volume by Price technical indicators',
        style: {
          color: '#9e9e9f',
          fontSize: '12',
        }
      },

      rangeSelector: {
        buttons: [
          {
            type: 'month',
            count: 1,
            text: '1m'
          },
          {
            type: 'month',
            count: 3,
            text: '3m'
          },
          {
            type: 'month',
            count: 6,
            text: '6m'
          },
          {
            type: 'ytd',
            text: 'YTD'
          }, {
            type: 'year',
            count: 1,
            text: '1y'
          }, {
            type: 'all',
            text: 'All'
          }],
        selected: 2,
        enabled: true,
        inputEnabled: true,
        // inputDateFormat: '%b %e, %Y %H:%M',
        allButtonsEnabled: true,

      },
      navigator: {
        enabled: true
      },
      xAxis: {

        ordinal: true,
        // navigator : true,

        type: 'datetime'

      },

      yAxis: [{
        startOnTick: false,
        endOnTick: false,
        labels: {
          align: 'right',
          x: -3
        },
        title: {
          text: 'OHLC'
        },
        height: '60%',
        lineWidth: 2,
        resize: {
          enabled: true
        },
        opposite: true
      }, {
        labels: {
          align: 'right',
          x: -3
        },
        title: {
          text: 'Volume'
        },
        top: '65%',
        height: '35%',
        offset: 0,
        lineWidth: 2,
        opposite: true
      }

      ],

      tooltip: {
        split: true
      },

      plotOptions: {
        series: {
          dataGrouping: {
            units: groupingUnits
          }
        }
      },

      series: [{
        type: 'candlestick',
        name: 'AAPL',
        id: 'aapl',
        zIndex: 2,
        data: ohlc
      }, {
        type: 'column',
        name: 'Volume',
        id: 'volume',
        data: volume,
        yAxis: 1
      }, {
        type: 'vbp',
        linkedTo: 'aapl',
        params: {
          volumeSeriesID: 'volume'
        },
        dataLabels: {
          enabled: false
        },
        zoneLines: {
          enabled: false
        }
      }, {
        type: 'sma',
        linkedTo: 'aapl',
        zIndex: 1,
        marker: {
          enabled: false
        }
      }]

    }
    this.showMainChart = true;
    console.log(this.showMainChart);

  }

  historySummaryCharts() {
    var list1 = [];


    for (var i = 0; i < this.historicDataSummary['c'].length; i++) {
      var tempList1 = [(this.historicDataSummary['t'][i] * 1000), this.historicDataSummary['c'][i]];
      list1[i] = tempList1;
    }

    Highcharts.setOptions({
      time: {
        timezoneOffset: 7 * 60
      }
    });

    this.chartOptionsSummary = {

      title: {
        text: this.tickerSymbol + " Hourly Price Variation",
        style: {
          color: '#9e9e9f',
          fontSize: '15',
        }
      },
      legend: { enabled: false },
      yAxis: [{

        title: {
          text: '',
        },
        opposite: true
      }],
      xAxis: [{
        scrollbar: {
          enabled: true
        },
        type: 'datetime',

        title: {
          text: '',
        }
      }],
      series: [{
        data: list1,
        type: 'line',
        color: this.change_percent ? "#28a745" : "#de3345"
      }]
    };
    this.showChart = true;
  }
  //Lolly

  //Lolly
  getWatchlist() {
    // console.log(this.inputEnteredTicker); 
    // console.log(localStorage.getItem(this.inputEnteredTicker));
    if (this.tickerSymbol === localStorage.getItem(this.tickerSymbol + "-Watchlist")) {
      // console.log('yes');
      this.display_filled_star = true;
      this.display_star = false;
    }
    else {
      this.display_filled_star = false;
      this.display_star = true;
    }
    // this.buyStock();


  }

  addToWatchlist() {
    if (this.display_star === true) {
      this.display_star = false;
      this.display_filled_star = true;
      this.wishlist_add = true;
      this.wishlist_remove = false;
      localStorage.setItem(this.tickerSymbol + "-Watchlist", this.tickerSymbol);
    }
    else {
      this.display_star = true;
      this.display_filled_star = false;
      this.wishlist_add = false
      this.wishlist_remove = true;
      localStorage.removeItem(this.tickerSymbol + "-Watchlist");

    }

  }
  //Lolly

  ngOnDestroy() {
    this.fetchSubscribe.unsubscribe();
  }
}
//'Range: 2010 to 2017' : TBD

