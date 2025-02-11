import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {Constants} from "../utilities/constants";

@Component({
  selector: 'app-dashboard-page',
  templateUrl: './dashboard-page.component.html',
  styleUrls: ['./dashboard-page.component.scss']
})
export class DashboardPageComponent {

  constructor (private router: Router){}

  public navigateToPieChartPage(){
    this.router.navigate([Constants.EXERCISE_25C]).then()
  }

}
