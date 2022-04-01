import { Component, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NavBarComponent } from './nav-bar/nav-bar.component';
import { SearchBarComponent } from './search-bar/search-bar.component';
import { PortfolioComponent } from './portfolio/portfolio.component';
import { WatchlistComponent } from './watchlist/watchlist.component';
import { SearchDetailsComponent } from './search-bar/search-details/search-details.component';

const routes: Routes = [
  { path: '', redirectTo: 'search/home', pathMatch: 'full' },
  // {
  //   path: '', component: SearchBarComponent,
  //   children: [
  //     { path: 'search/home', component: SearchBarComponent },
  //     { path: 'search/:tickerVal', component: SearchDetailsComponent }
  //   ]
  // },

  {
    path: 'search', component: SearchBarComponent,
    children: [
      { path: 'home', component: SearchBarComponent },
      { path: ':tickerVal', component: SearchDetailsComponent }
    ]
  },
  
  { path: 'search/:tickerVal', component: SearchDetailsComponent },
  { path: 'watchlist', component: WatchlistComponent },
  { path: 'portfolio', component: PortfolioComponent }


  // {

  //   path: '', redirectTo: '/search/home', pathMatch: 'full',component: SearchBarComponent,
  //   children: [
  //     { path: 'search/home', component: SearchBarComponent },
  //     { path: 'search/:tickerVal', component: SearchDetailsComponent }
  //   ]
  // },
  // { path: 'watchlist', component: WatchlistComponent },
  // { path: 'portfolio', component: PortfolioComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
