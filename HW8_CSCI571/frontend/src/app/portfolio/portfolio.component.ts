import { Component, OnInit } from '@angular/core';
import { NgbModal, ModalDismissReasons, NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { HttpService } from '../shared/http.service';
import { Subject, combineLatest } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
@Component({
  selector: 'app-portfolio',
  templateUrl: './portfolio.component.html',
  styleUrls: ['./portfolio.component.css']
})
export class PortfolioComponent implements OnInit {
  moneyInWallet: any;  //TBD ; change moneyLeft to this
  // alertPortfolio: any;
  public closeResult: string = '';
  isBuyStock = false;
  stockPrice: any;
  companyDescription: any;
  tickerSymbol: string = '';
  // portfolioStockData: any =[];
  portfolioStockDataMap = new Map();
  tickerVal = ''
  currentStockModal = '';
  currentStockModalData = '';
  stockPriceC: any;
  cName: any;

  //Lolly
  private _buySuccess = new Subject<string>();
  private _sellSuccess = new Subject<string>();
  buyMessage = '';
  sellMessage = '';
  // moneyLeft: any;
  alertPortfolio = false;
  stockBuy: any = false;
  public closeModal: string = '';
  countItem: any = 0;
  enteredQuantity: number = 0;
  enteredQuantitySell: number = 0;
  totalPrice: string = "0.00";
  // Current_Price: any;
  removePortfolio = false;
  // name: any = "";
  stockLeft: any;
  portfolio_add: any;
  portfolio_remove: any;
  //Lolly
  // portfolioStocks = ['TSLA' : 5, 'AAPL' :2];

  constructor(private modalService: NgbModal, private httpService: HttpService) { }

  ngOnInit(): void {
    // this.moneyInWallet = "25000.00";
    // this.alertPortfolio = true;
    // localStorage.setItem('moneyInWallet', this.moneyInWallet);
    // let am1 = 4000;
    // let localList1 = {"ticker" : "TSLA", "qty" : 4, "amount" : am1};
    // let localList2 = '{"ticker" : "AAPL", "qty" : 3, "amount" : 1500}';


    // localStorage.setItem('TSLA-Port', JSON.parse({"ticker" : "TSLA", "qty" : 4}));
    // localStorage.setItem('TSLA-Port', JSON.stringify(localList1));
    // localStorage.setItem('AAPL-Port', localList2);
    // let temp : any = localStorage.getItem('TSLA-Port');
    // console.log('itreme> ' + temp + ' ' + JSON.parse(temp).ticker);
    // parse(text: string, reviver?: ((this: any, key: string, value: any) => any) | undefined): any

    // console.log('portfolioStockData>> '+JSON.stringify(this.portfolioStockData));

    // this.httpService.getData('stockPrice', this.tickerSymbol).subscribe(res => this.stockPrice = res);

    if (localStorage.getItem('moneyInWallet')) {
      let money: any = localStorage.getItem('moneyInWallet')
      this.moneyInWallet = parseFloat(money);
    }
    else {
      localStorage.setItem('moneyInWallet', "25000.00");
      this.moneyInWallet = localStorage.getItem('moneyInWallet')
    }

    this.loadPortfolioStockData();

    // Object.keys(localStorage).forEach(data => {


    //   if (data.includes("-Portfolio")) {
    //     this.countItem += 1;

    //     let stockValJson: any = localStorage.getItem(data);
    //     console.log(stockValJson)
    //     this.stockPrice = JSON.parse(stockValJson);

    //     this.loadPortfolioStockData();
    //     // console.log(this.portfolioVal);
    //   }

    // });

    this.loadPortfolioStockData();
    if (this.countItem === 0) {
      this.alertPortfolio = true;
    }

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




  }

  loadPortfolioStockData() {
    Object.keys(localStorage).forEach(data => {
      if (data.includes('-Portfolio')) {
        this.companyDescription = [];
        this.stockPrice = [];
        this.countItem += 1;
        let item: any = localStorage.getItem(data);
        // let portData = item;
        this.httpService.getData('companyDescription', JSON.parse(item).ticker).subscribe(res => {
          this.companyDescription = res;
          // console.log('this.companyDescription>'+JSON.stringify(this.companyDescription));
          this.httpService.getData('stockPrice', JSON.parse(item).ticker).subscribe(res => {
            let tickerVal = JSON.parse(item).ticker;
            let quantity = JSON.parse(item).qty;
            let amt = JSON.parse(item).amount;
            this.stockPrice = res;
            this.stockPriceC = this.stockPrice.c;
            this.cName = this.companyDescription.name;
            this.portfolioStockDataMap.set(tickerVal, {
              tickerVal: tickerVal,
              companyName: this.cName,
              currentPrice: this.stockPriceC,
              quantity: quantity,
              totalCost: amt

            });
            // console.log('portfolioStockData>> '+JSON.stringify(this.portfolioStockData));
            // console.log('portfolioStockDataMap>> '+(this.portfolioStockDataMap));
            // console.log('portfolioStockDataMap>> '+(JSON.stringify(this.portfolioStockDataMap.get('TSLA'))));
            // console.log('portfolioStockDataMap>> '+(JSON.stringify(this.portfolioStockDataMap.get('AAPL'))));
          });
        });

      }
    });
  }


  openPortfolioBuy(portfolioModalBuy: any, stockDeal: any, val_port: any) {
    console.log('stockDeal> '+stockDeal);
    console.log('val_port> '+val_port);
    this.enteredQuantity = 0
    if (stockDeal === 'buy') {
      this.stockBuy = true;
    }
    else {
      this.stockBuy = false;
    }
    this.tickerSymbol = val_port;
    // this.tickerSymbol = val_port.ticker;
    let money: any = localStorage.getItem('moneyInWallet')
    this.moneyInWallet = parseFloat(money);

    this.modalService.open(portfolioModalBuy, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {
      this.closeModal = `Closed with: ${result}`;
    }, (reason) => {
      this.closeModal = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  openPortfolioSell(portfolioModalSell: any, stockDeal: any, val_port: any) {
    this.enteredQuantitySell = 0
    if (stockDeal === 'buy') {
      this.stockBuy = true;
    }
    else {
      this.stockBuy = false;
    }
    // this.tickerSymbol = val_port.ticker;
    this.tickerSymbol = val_port;
    let money: any = localStorage.getItem('moneyInWallet')
    this.moneyInWallet = parseFloat(money);
    let stockValJson: any = localStorage.getItem(this.tickerSymbol + "-Portfolio");
    let stockVal = JSON.parse(stockValJson);
    this.stockLeft = stockVal.quantity;

    this.modalService.open(portfolioModalSell, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {
      this.closeModal = `Closed with: ${result}`;
    }, (reason) => {
      this.closeModal = `Dismissed ${this.getDismissReason(reason)}`;
    });
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

  buyStock(tickerSymbol: any) {
    // this.modal.close(this.portfolioModal)
    this._buySuccess.next('Message successfully changed.');
    this.portfolio_add = true;
    this.tickerSymbol = tickerSymbol;
    if (this.enteredQuantity > 0) {
      let total = this.stockPriceC * this.enteredQuantity;
      console.log(total);
      let val: any = localStorage.getItem('moneyInWallet')
      let wallet: any = parseFloat(val);
      wallet = wallet - total;
      localStorage.setItem('moneyInWallet', wallet.toFixed(2).toString());
      // console.log(wallet);
      // if((localStorage.getItem(this.inputEnteredTicker+"-Portfolio"))){
      if (localStorage.getItem(this.tickerSymbol + "-Portfolio")) {
        console.log(this.tickerSymbol)
        let stockValJson: any = localStorage.getItem(this.tickerSymbol + "-Portfolio");
        let stockVal = JSON.parse(stockValJson);
        console.log(stockVal);
        let quantity = this.enteredQuantity + stockVal.qty;
        total = total + stockVal.amount;
        console.log(this.enteredQuantity);
        console.log(total);
        console.log('lolly> '+total+'stockVal>   '+JSON.stringify(stockVal));
        localStorage.setItem(this.tickerSymbol + "-Portfolio", JSON.stringify({ "ticker": this.tickerSymbol, "qty": quantity, "amount": total }))
        this.portfolioStockDataMap.set(stockVal.ticker, {
          tickerVal: this.tickerSymbol,
          companyName: this.cName,
          currentPrice: this.stockPriceC,
          quantity: quantity,
          totalCost: total
        })
        console.log('portfolioStockDataMap>' +this.portfolioStockDataMap);
      }
      // else {
      //   localStorage.setItem(this.tickerSymbol + "-Portfolio", JSON.stringify({ "ticker": this.tickerSymbol, "quantity": this.enteredQuantity, "totalPrice": total }))
      // }

      // console.log(localStorage.getItem(this.inputEnteredTicker+"-Portfolio"));
      //To update the cards

      // this.fetchPortfolio(stockVal);
      // this.portfolioVal[stockVal.ticker][]
      //   this.portfolioVal.set( stockVal.ticker, {
      //     ticker : this.selectedTicker,
      //     totalPrice : stockVal.total,
      //     quantity : stockVal.quantity,
      //     name: name, 
      //     currPrice : this.latestPrice.c,



      // }
      // if(stockVal.quantity > 0){
      //   this.sell_button = true;
      //   console.log(this.sell_button)
      // }

      // this.modal.close();
    }
    let money: any = localStorage.getItem('moneyInWallet')
    this.moneyInWallet = parseFloat(money);

  }

  sellStock(tickerSymbol: any) {
    this._sellSuccess.next('Message successfully changed.');
    this.portfolio_remove = true;
    this.tickerSymbol = tickerSymbol;
    if (this.enteredQuantitySell > 0) {
      let total = this.stockPriceC * this.enteredQuantitySell;
      console.log(total);
      let val: any = localStorage.getItem('moneyInWallet')
      let wallet: any = parseFloat(val);
      wallet = wallet + total;
      localStorage.setItem('moneyInWallet', wallet.toFixed(2).toString());
      let stockValJson: any = localStorage.getItem(this.tickerSymbol + "-Portfolio");
      let stockVal = JSON.parse(stockValJson);
      // let quantity = this.enteredQuantitySell + stockVal.quantity;
      let stockQ = stockVal.qty - this.enteredQuantitySell;
      total = stockVal.amount - total
      localStorage.setItem(this.tickerSymbol + "-Portfolio", JSON.stringify({ "ticker": this.tickerSymbol, "qty": stockQ, "amount": total }))
      //To update the cards
      stockValJson = localStorage.getItem(this.tickerSymbol + "-Portfolio");
      stockVal = JSON.parse(stockValJson);
      // this.fetchPortfolio(stockVal);
      if (stockQ > 0) {
        this.portfolioStockDataMap.set(stockVal.ticker, {
          tickerVal: this.tickerSymbol,
          companyName: this.cName,
          currentPrice: this.stockPriceC,
          quantity: stockQ,
          totalCost: total
        })
      }
      else {
        // this.sell_button = false;
        this.removePortfolio = true;
        localStorage.removeItem(this.tickerSymbol + "-Portfolio")
        this.portfolioStockDataMap.delete(this.tickerSymbol)
        // this.alertPortfolio = true;

      }
      if (this.portfolioStockDataMap.size === 0) {
        this.alertPortfolio = true;
      }
      let money: any = localStorage.getItem('moneyInWallet')
      this.moneyInWallet = parseFloat(money);
    }
  }


}


