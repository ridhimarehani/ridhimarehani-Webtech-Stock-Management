import { Component, OnInit } from '@angular/core';
import { StatePreserveService } from '../state-preserve.service';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit {
  public data: string = '';
  public isCollapsed = false;
  // is
  constructor(private statePreseve: StatePreserveService) { }

  ngOnInit(): void {

    this.statePreseve.tickerSymbolSubject.subscribe(res => {
      this.data = res;
      console.log('state>> ',res);
    }
    )
  }
}
