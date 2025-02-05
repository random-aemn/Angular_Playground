import { Component } from '@angular/core';
import {MyTabService} from "../../services/my-tab.service";

@Component({
  selector: 'app-tab4-component',
  templateUrl: './tab4-component.component.html',
  styleUrls: ['./tab4-component.component.scss']
})
export class Tab4ComponentComponent  {

  constructor(private myTabService: MyTabService) {
  }

  public ngOnInit(){

  }
}
