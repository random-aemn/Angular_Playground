import {Injectable} from '@angular/core';
import {navbarContents} from "../layout/navbar/navbar-contents";
import {BehaviorSubject, debounceTime, Observable} from "rxjs";
import {PreferenceService} from "./preference.service";
import {Constants} from "../utilities/constants";
import {GetOnePreferenceDTO} from "../models/preferences/get-one-preference-dto";
import {UserService} from "./user.service";
import {UserInfoDTO} from "../models/user-info-dto";
import {NavGroup} from "../models/navbar/nav-group-dto";
import {NavItem} from "../models/navbar/nav-item-dto";
import {NavbarState} from "../models/navbar-state";

@Injectable({
	providedIn: 'root'
})
export class NavbarService {

	private navbarExtendedSubject: BehaviorSubject<NavbarState>;

	public navbarContentsLimitedByRole: NavGroup[];

	public constructor(private preferenceService: PreferenceService,
					           private userService: UserService) {}


	private convertStringToBooleanOrReturnTrue(value: string): boolean {
		if (value === 'false') {
			return false;
		} else {
			// 'true' is the default - even if a preference doesn't exist
			return true;
		}
	}


	public getNavbarStateAsObservable(): Observable<NavbarState> {
		return this.navbarExtendedSubject.asObservable();
	}


	// This service does not work until THIS method is called - ensure it's called early in page load (app.component.html)
	public initializeService(aValue: GetOnePreferenceDTO): void {

      // Initialize the state after we've queried the preferences table
      let initialState: NavbarState = new NavbarState();
      initialState.isAppNavbarExtended = this.convertStringToBooleanOrReturnTrue(aValue.value);

      // Adjust the navbar contents to contain the user's full name
      this.userService.getLoggedInUserInfo().subscribe((aUserInfoDTO: UserInfoDTO)=>{

          // Get the hardcoded navbar contents from the class in layout/navbar and update the user's name
          // This works because initializeService executes BEFORE the HTML is rendered
          navbarContents[navbarContents.length - 1].navGroupName = aUserInfoDTO.loggedInFullName;

          // Use the user's authorized page routes to *LIMIT* what navbarObjects the user can see
          this.navbarContentsLimitedByRole = this.generateArrayOfNavbarObjectsBasedOnAllowedRoutes(navbarContents, aUserInfoDTO.pageRoutes);
      });

    // Anyone who is listening will get this initial value
    this.navbarExtendedSubject = new BehaviorSubject(initialState);
	}


	public getNavbarStateResizeEventAsObservable(): Observable<NavbarState> {
		// Accounts for the 500ms animation delay (subscribe to this when you need something to resize after a navbar mode change)
		return this.navbarExtendedSubject.asObservable().pipe(
			debounceTime(500));
	}

	public updateNavbarState(aNewValue: boolean): void {
		let navbarState: NavbarState = new NavbarState();
		navbarState.isAppNavbarExtended = aNewValue;

		// Update the state in the preferences table so things are persistent
		this.preferenceService.setPreferenceValueWithoutPage(Constants.NAVBAR_EXTENDED_STATE_PREFERENCE_NAME, aNewValue).subscribe()

        // Send the message that indicates to the rest of the app that the navbar has updated
		this.navbarExtendedSubject.next(navbarState);
	}


  /*
   * Generate an array of navbar objects (limited based on the the user's allowed routes)
   */
  private generateArrayOfNavbarObjectsBasedOnAllowedRoutes(aAllNavbarObjects: NavGroup[], aPageRoutes: Map<string, boolean>): NavGroup[] {

    let allowedNavbarObjects: NavGroup[] = [];

    // Loop through each navbar group
    for (let navbarGroup of aAllNavbarObjects) {

      // Loop through the children of this navbar group
      let currentAllowedNavbarGroup: NavGroup | null = null;

      for (let navItem of navbarGroup.navGroupItems) {
        // Get the route of this navbar item
        let routeUrl: string = navItem.navItemUrl;

        // Determine if this route is allowed
        let routeAllowed: boolean | undefined = aPageRoutes.get(routeUrl);

        if (routeAllowed) {
          // This route is allowed.  So, add it the list

          // Create the nav item
          let allowedNavItem: NavItem = new NavItem();
          allowedNavItem.navItemName = navItem.navItemName;
          allowedNavItem.navItemUrl = navItem.navItemUrl;

          if (currentAllowedNavbarGroup == null) {
            currentAllowedNavbarGroup = new NavGroup();
            currentAllowedNavbarGroup.navGroupItems = [];
            currentAllowedNavbarGroup.navGroupName = navbarGroup.navGroupName;
            currentAllowedNavbarGroup.faIconTag    = navbarGroup.faIconTag;
          }

          // Add the item to the navbarGroup
          currentAllowedNavbarGroup.navGroupItems.push(allowedNavItem);
        }

      } // end of looping through the children

      if (currentAllowedNavbarGroup != null) {
        allowedNavbarObjects.push(currentAllowedNavbarGroup);
      }

    } // end of looping through the navbar groups

    return allowedNavbarObjects;

  }

}
