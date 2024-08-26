import {Component, OnInit} from '@angular/core';
import {HeaderUrlDTO} from "../../models/header-url-dto";
import {HeaderService} from "../../services/header.service";
import {Observable} from "rxjs";
import {UserInfoDTO} from "../../models/user-info-dto";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.scss']
})
export class UserSettingsComponent implements OnInit {

  public userInfoObs: Observable<UserInfoDTO>;
  public listOfHeaderDTO: HeaderUrlDTO[];

  constructor(private headerService: HeaderService,
              private userService:   UserService) { }


  public ngOnInit() {
    this.userInfoObs = this.userService.getLoggedInUserInfo();
    this.listOfHeaderDTO = this.headerService.getHeaderThemeDTOs();
  }

  private unselectAllHeaderDTOs(): void {
    for (let headerDTO of this.listOfHeaderDTO) {
      headerDTO.isSelected = false;
    }
  }

  public themeButtonClicked(headerDTO: HeaderUrlDTO) {
    this.unselectAllHeaderDTOs();
    headerDTO.isSelected = true;
    this.headerService.updateHeaderThemeDTO(headerDTO);
  }


}
