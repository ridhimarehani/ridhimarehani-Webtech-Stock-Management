import { Component, OnInit } from '@angular/core';
import { NgbModal, ModalDismissReasons, NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { HttpService } from '../shared/http.service';

@Component({
  selector: 'app-portfolio',
  templateUrl: './portfolio.component.html',
  styleUrls: ['./portfolio.component.css']
})
export class PortfolioComponent implements OnInit {
  moneyInWallet: any = 0
  alertPortfolio: any;
  public closeResult: string = '';
  isBuyStock = false;
  stockPrice: any;
  companyDescription: any;
  tickerSymbol: string = 'TSLA';
  // portfolioStockData: any =[];
  portfolioStockDataMap = new Map();
  tickerVal = ''
  currentStockModal = '';
  currentStockModalData = '';
  // portfolioStocks = ['TSLA' : 5, 'AAPL' :2];

  constructor(private modalService: NgbModal, private httpService: HttpService) { }

  ngOnInit(): void {
    this.moneyInWallet = "25000.00"
    this.alertPortfolio = true
    let am1 = 4000;
    let localList1 = {"ticker" : "TSLA", "qty" : 4, "amount" : am1};
    let localList2 = '{"ticker" : "AAPL", "qty" : 3, "amount" : 1500}';
    localStorage.setItem('moneyInWallet', this.moneyInWallet);
    // localStorage.setItem('TSLA-Port', JSON.parse({"ticker" : "TSLA", "qty" : 4}));
    localStorage.setItem('TSLA-Port', JSON.stringify(localList1));
    localStorage.setItem('AAPL-Port', localList2);
    let temp : any = localStorage.getItem('TSLA-Port');
    // console.log('itreme> ' + temp + ' ' + JSON.parse(temp).ticker);
    // parse(text: string, reviver?: ((this: any, key: string, value: any) => any) | undefined): any
    this.loadPortfolioStockData();
    // console.log('portfolioStockData>> '+JSON.stringify(this.portfolioStockData));

    // this.httpService.getData('stockPrice', this.tickerSymbol).subscribe(res => this.stockPrice = res);

  }

  loadPortfolioStockData() {
    Object.keys(localStorage).forEach(data => {
      if (data.includes('Port')) {
        this.companyDescription = [];
        this.stockPrice = [];
        let item : any = localStorage.getItem(data);
        // let portData = item;
        this.httpService.getData('companyDescription', JSON.parse(item).ticker).subscribe(res => {
          this.companyDescription = res;
          // console.log('this.companyDescription>'+JSON.stringify(this.companyDescription));
          this.httpService.getData('stockPrice', JSON.parse(item).ticker).subscribe(res => {
            let tickerVal = JSON.parse(item).ticker;
            let quantity = JSON.parse(item).qty;
            let amt = JSON.parse(item).amount;
            this.stockPrice = res;
            // this.portfolioStockData.push({
            //     tickerVal : tickerVal,
            //     companyName: this.companyDescription.name,
            //     currentPrice : this.stockPrice.c,
            //     quantity : quantity,
            //     totalCost : amt
              
            // });
            this.portfolioStockDataMap.set(tickerVal, {
              tickerVal : tickerVal,
              companyName: this.companyDescription.name,
              currentPrice : this.stockPrice.c,
              quantity : quantity,
              totalCost : amt
            
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
  openPortfolioBuy(content: any, transactionType: string, tickerVal :string) {
    this.isBuyStock = (transactionType == "buyStock") ? true : false;
    this.currentStockModal = tickerVal;
    this.currentStockModalData = this.portfolioStockDataMap.get(tickerVal);
    console.log('currentStockModalData>> '+JSON.stringify(this.currentStockModalData));
    // if (transactionType == "buyStock") ? (this.isBuyStock = true) : (this.isBuyStock = false);
    this.modalService.open(content, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
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

}
