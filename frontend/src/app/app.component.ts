import {Component, OnInit} from '@angular/core';
import {NavbarService} from "./services/navbar.service";
import {Observable, tap} from "rxjs";
import {NavbarState} from "./models/navbar-state";
import {UserInfoDTO} from "./models/user-info-dto";
import {UserService} from "./services/user.service";
import {PreferenceService} from "./services/preference.service";
import {Constants} from "./utilities/constants";
import {GetOnePreferenceDTO} from "./models/preferences/get-one-preference-dto";
import {HeaderService} from "./services/header.service";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ErrorDialogComponent} from "./errorHandler/error-dialog/error-dialog.component";
import {HttpErrorResponse} from "@angular/common/http";
import {ErrorDialogFormData} from "./errorHandler/error-dialog-form-data";
import {ErrorService} from "./errorHandler/error.service";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  public navBarExtended: boolean;
  public userInfoObs: Observable<UserInfoDTO>;
  public navbarPreferenceObs: Observable<GetOnePreferenceDTO>;
  public headerPreferenceObs: Observable<GetOnePreferenceDTO>;

  private errorDialogIsOpen: boolean = false;
  private errorDialogRef:    MatDialogRef<ErrorDialogComponent>;

  public constructor(private navbarService: NavbarService,
                     private preferenceService: PreferenceService,
                     private userService: UserService,
                     private headerService: HeaderService,
                     private errorService: ErrorService,
                     private matDialog: MatDialog) {
  }


  public ngOnInit(): void {
    this.userInfoObs = this.userService.getLoggedInUserInfo();

    // Initialize the navbar service before the HTML or it won't work because pages will call null instead of a subject
    this.navbarPreferenceObs = this.preferenceService.getPreferenceValueWithoutPage(Constants.NAVBAR_EXTENDED_STATE_PREFERENCE_NAME)
      .pipe(tap((aValue: GetOnePreferenceDTO) => {
        // Initialize the behavior subject storing the navbar state in the shared service
        this.navbarService.initializeService(aValue);

        // Get the state of the navbar from the shared service only AFTER the preference table response came back
        this.navbarService.getNavbarStateAsObservable().subscribe((aDTO: NavbarState) => {
            this.navBarExtended = aDTO.isAppNavbarExtended
          }
        )
      })); // End of observable

    // Initialize the header service before the HTML or it won't work because pages will call null instead of a subject
    this.headerPreferenceObs = this.preferenceService.getPreferenceValueWithoutPage(Constants.HEADER_THEME_PREFERENCE_NAME)
      .pipe(tap((aValue: GetOnePreferenceDTO) => {
        // Initialize the behavior subject storing the header state in the shared service
        this.headerService.initializeService(aValue);
      })); // End of observable



    this.errorService.getErrorsAsObservable().subscribe( (aError: HttpErrorResponse) => {
      // An error came in.  So, display the error in a popup

      // Create the form data object (to pass-in to the dialog box)
      let errorFormData: ErrorDialogFormData = new ErrorDialogFormData();
      errorFormData.error_text = aError.statusText;
      errorFormData.status_code = aError.status
      errorFormData.url = aError.url;

      if (typeof aError.error === 'object') {
        // The aError.error is an object.  So, pull the error from aError.error.message
        errorFormData.message = aError.error.message;
      }
      else {
        // The aError.error is not an object.  So, pull the error from aError.error
        errorFormData.message = aError.error;
      }

      if (this.errorDialogIsOpen) {
        // The error dialog is already open -- so close it
        this.errorDialogRef.close(false);
      }

      this.errorDialogIsOpen = true;

      // Open the Error Dialog
      // Do not set the height of dialog boxes.  Let them grow
      this.errorDialogRef = this.matDialog.open(ErrorDialogComponent, {
        minWidth: '400px',
        maxWidth: '800px',
        data: errorFormData
      });

      this.errorDialogRef.afterClosed().subscribe(() => {
        // The error dialog box has closed
        this.errorDialogIsOpen = false;
      });

    });
  }

}
