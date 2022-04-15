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
  changeInPort = 0;

  //Lolly
  private buySuccessMessage = new Subject<string>();
  private sellSuccessMessage = new Subject<string>();
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

    if (localStorage.getItem('moneyInWallet')) {
      let money: any = localStorage.getItem('moneyInWallet')
      this.moneyInWallet = parseFloat(money);
    }
    else {
      localStorage.setItem('moneyInWallet', "25000.00");
      this.moneyInWallet = localStorage.getItem('moneyInWallet')
    }

    // this.loadPortfolioStockData();

    this.loadPortfolioStockData();
    console.log('portfolioStockDataMap11>>' + JSON.stringify(this.portfolioStockDataMap));

    this.showAlerts();



  }

  showAlerts() {

    if (this.countItem === 0) {
      this.alertPortfolio = true;
    }

    this.buySuccessMessage.subscribe(
      (message) => (this.buyMessage = message)
    );

    this.sellSuccessMessage.subscribe(
      (message) => (this.sellMessage = message)
    );

    this.buySuccessMessage
      .pipe(debounceTime(5000))
      .subscribe(() => (this.buyMessage = ''));


    this.sellSuccessMessage
      .pipe(debounceTime(5000))
      .subscribe(() => (this.sellMessage = ''));
  }

  loadPortfolioStockData() {
    Object.keys(localStorage).forEach(data => {
      if (data.includes('-Portfolio')) {
        // this.companyDescription = [];
        this.stockPrice = [];
        this.countItem += 1;
        let item: any = localStorage.getItem(data);
        // let portData = item;
        let itemParsed = JSON.parse(item);
        // console.log('itemmm> ', itemParsed.qty != null && itemParsed.amount != null);
        if (itemParsed.qty != null && itemParsed.amount != null) {
          // console.log('ssssssss');
          // console.log('JSON.parse(item).ticker>' + JSON.parse(item).ticker);
          this.httpService.getData('companyDescription', itemParsed.ticker).subscribe(res => {
            this.companyDescription = res;
            // console.log('resMe> ' + JSON.stringify(res));
            this.cName = this.companyDescription.name;
            // console.log('resMe> ' + this.companyDescription.name);

            // console.log('this.companyDescriptionMap>' + JSON.stringify(this.companyDescription));
            this.httpService.getData('stockPrice', JSON.parse(item).ticker).subscribe(res => {
              // console.log('resYouu> ' + this.companyDescription.name);
              let tickerVal = itemParsed.ticker;
              let quantity = itemParsed.qty;
              let amt = itemParsed.amount;
              this.stockPrice = res;
              this.stockPriceC = this.stockPrice.c;
              this.cName = this.companyDescription.name;
              this.changeInPort = Math.round(((this.stockPriceC - amt / quantity) + Number.EPSILON) * 100) / 100;
              // console.log('this.companyDescriptionMapMEE>' + this.cName);
              this.portfolioStockDataMap.set(tickerVal, {
                tickerVal: tickerVal,
                companyName: this.cName,
                currentPrice: this.stockPriceC,
                quantity: quantity,
                totalCost: amt,
                changeInPort: this.changeInPort

              });

              // console.log('portfolioStockDataMapAA>>' + (JSON.stringify(this.portfolioStockDataMap.get(tickerVal))));
              // console.log('portfolioStockDataMapAA>>' + (JSON.stringify(this.portfolioStockDataMap.get('TSLA'))));
            });
          });

        }


      }
    });
  }


  openPortfolioBuy(portfolioModalBuy: any, stockDeal: any, val_port: any) {
    // console.log('stockDeal> ' + stockDeal);
    // console.log('val_port> ' + val_port);
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
    this.stockLeft = stockVal.qty;

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
    this.buySuccessMessage.next('Message successfully changed.');
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
        console.log('lolly> ' + total + 'stockVal>   ' + JSON.stringify(stockVal));
        localStorage.setItem(this.tickerSymbol + "-Portfolio", JSON.stringify({ "ticker": this.tickerSymbol, "qty": quantity, "amount": total }))
        // this.changeInPort = this.stockPriceC-total/quantity;
        this.changeInPort = Math.round(((this.stockPriceC - total / quantity) + Number.EPSILON) * 100) / 100;
        let portfolData = this.portfolioStockDataMap.get(this.tickerSymbol);
        this.cName = portfolData.companyName;
        this.portfolioStockDataMap.set(this.tickerSymbol, {
          tickerVal: this.tickerSymbol,
          companyName: this.cName,
          currentPrice: this.stockPriceC,
          quantity: quantity,
          totalCost: total,
          changeInPort: this.changeInPort
        })
        console.log('portfolioStockDataMap>' + this.portfolioStockDataMap);
      }
    }
    let money: any = localStorage.getItem('moneyInWallet')
    this.moneyInWallet = parseFloat(money);

    this.modalService.dismissAll();

  }

  sellStock(tickerSymbol: any) {
    this.sellSuccessMessage.next('Message successfully changed.');
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
      let quantity = stockVal.qty - this.enteredQuantitySell;
      total = stockVal.amount - total
      localStorage.setItem(this.tickerSymbol + "-Portfolio", JSON.stringify({ "ticker": this.tickerSymbol, "qty": quantity, "amount": total }))
      //To update the cards
      stockValJson = localStorage.getItem(this.tickerSymbol + "-Portfolio");
      stockVal = JSON.parse(stockValJson);
      // this.changeInPort = this.stockPriceC-total/stockQ;
      this.changeInPort = Math.round(((this.stockPriceC - total / quantity) + Number.EPSILON) * 100) / 100;
      // this.fetchPortfolio(stockVal);

      if (quantity > 0) {
        let portfolData = this.portfolioStockDataMap.get(this.tickerSymbol);
        this.cName = portfolData.companyName;
        this.portfolioStockDataMap.set(this.tickerSymbol, {
          tickerVal: this.tickerSymbol,
          companyName: this.cName,
          currentPrice: this.stockPriceC,
          quantity: quantity,
          totalCost: total,
          changeInPort: this.changeInPort
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
    this.modalService.dismissAll();
  }


}


