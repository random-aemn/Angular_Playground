import {Component, OnInit} from '@angular/core';
import {HeaderUrlDTO} from "../../models/header-url-dto";
import {HeaderService} from "../../services/header.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  public headerUrlDTO: HeaderUrlDTO;

  public constructor(private headerService: HeaderService) {}

  public ngOnInit() {
    // Get the state of the navbar from the shared service only AFTER the preference table response came back
    this.headerService.getHeaderObservable().subscribe((aDTO: HeaderUrlDTO) => {
        this.headerUrlDTO = aDTO;
      }
    )
  }
}
