import { Component } from '@angular/core';
import {FormUtilsService} from "../services/form-utils.service";

@Component({
  selector: 'app-scroll-into-view',
  templateUrl: './scroll-into-view.component.html',
  styleUrls: ['./scroll-into-view.component.scss']
})
export class ScrollIntoViewComponent {

  constructor(
    public formUtilsService: FormUtilsService,
  ) {}

}
