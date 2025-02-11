import { Component } from '@angular/core';
import {MyTabService} from "../../services/my-tab.service";

@Component({
  selector: 'app-tab1-component',
  templateUrl: './tab1-component.component.html',
  styleUrls: ['./tab1-component.component.scss']
})
export class Tab1ComponentComponent {

  constructor(public myTabService: MyTabService) {
  }
}
