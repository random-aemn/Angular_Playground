import { Component } from '@angular/core';


@Component({
  selector: 'app-stock-trades',
  templateUrl: './stock-trades.component.html',
  styleUrls: ['./stock-trades.component.scss']
})
export class StockTradesComponent {

public orderType: number = 0;

  public saveFormHyperlink(){

    console.log("saved for later was clicked")

  }
}
