import { Component } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AcknowledgementService} from "../services/acknowledgement.service";

@Component({
  selector: 'app-acknowledgement-page',
  templateUrl: './acknowledgement-page.component.html',
  styleUrls: ['./acknowledgement-page.component.scss']
})
export class AcknowledgementPageComponent {

  constructor(private acknowledgementService: AcknowledgementService) { }


  public acknowledgeClicked(){

    // Must subscribe to actually invoke the rest call.  Without the subscription, the observable is just a lazy stream
    this.acknowledgementService.markUserAsAcknowledged().subscribe();
  }

}
