import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavbarState} from "../../../models/navbar-state";
import {NavbarService} from "../../../services/navbar.service";
import {NavGroup} from "../../../models/navbar/nav-group-dto";
import {NavItem} from 'src/app/models/navbar/nav-item-dto';
import {NavigationEnd, Router} from "@angular/router";
import {Subscription} from "rxjs";

@Component({
	selector: 'app-navbar-collapsed',
	templateUrl: './navbar-collapsed.component.html',
	styleUrls: ['./navbar-collapsed.component.scss']
})
export class NavbarCollapsedComponent implements OnInit, OnDestroy {

	// Navbar states (for changing extended/collapsed modes)
	public navBarExtended: boolean;

	// These store the LAST/PREVIOUSLY opened nav item or group
	private openedNavItem: NavItem;
	private openedNavGroup: NavGroup;

	private routerSubscription: Subscription;
	private navbarAnimationSubjectSubscription: Subscription;
	private navbarEventSubjectSubscription: Subscription;

  // Define the navbar groups and their properties here
  public navBarObjects: NavGroup[];

  constructor(private navbarService: NavbarService,
		      		private router: Router) {

    // Get the navbar contents from the navbarService
    this.navBarObjects = this.navbarService.navbarContentsLimitedByRole;

		// Set the index of the items in the shared navbarContents object in the constructor
		// 		(we do this here to make the navContents class simpler/easier to read)
		for (let [index, navbarObject] of this.navBarObjects.entries()) {
			navbarObject.navGroupIndex = index;
			for (let [innerIndex, navbarChildObject] of navbarObject.navGroupItems.entries()) {
				navbarChildObject.navItemIndex = innerIndex;
			}
		}
	}  // End of constructor


	private flipAllOpenStatesToFalse(): void {
		// Set the opened child element's opened state to false (if it exists)
		if (this.openedNavItem) {
			this.openedNavItem.navItemOpenedState = false;
		}

		// Set the opened parent element's opened state to false (if it exists)
		if (this.openedNavGroup) {
			this.openedNavGroup.navGroupOpenedState = false;
		}
	}  // End of flipAllOpenStatesToFalse


	private handlePotentialNavSelectionHighlightingChange(): void {
		// This function is called whenever the router navigates somewhere else - or when the user first hits the site
		// It will automatically determine which nav-element is highlighted
		// We're using this instead of click-handlers to capture router changes EVEN when they're not caused by the nav

		let currentUrl = this.router.url;

		if (currentUrl == '/') {
			// The router was called because the site was visited for the first time or after a refresh - ignore it...
			//		...it will cycle back again with the actual URL
			return
		}

		// Make sure all the nav-states are false
		this.flipAllOpenStatesToFalse();

		if (!currentUrl.includes('403') && !this.navBarExtended) {
			// Exclude 403 pages (no selection highlighting for 403 response)
			for (let navGroupObject of this.navBarObjects) {
				// Iterate through all the children of this object to see if the user is at the corresponding page
				for (let navChildObject of navGroupObject.navGroupItems) {
					if (currentUrl == ('/' + navChildObject.navItemUrl)) {
						// We're only doing selection highlighting on the parent in this mode - mark the parent as open
						navGroupObject.navGroupOpenedState = true;
						this.openedNavGroup = navGroupObject;
					}
				}
			}
		}
	} // End of handlePotentialNavSelectionHighlightingChange


	private closeAllNavGroups() {
		// Close all nav groups
		for (let navGroup of this.navBarObjects) {
			navGroup.navGroupOpenedState = false;
			navGroup.showChildren = false;
			for (let navGroupChild of navGroup.navGroupItems) {
				navGroupChild.navItemOpenedState = false;
			}
		}
		this.flipAllOpenStatesToFalse();
	} // End of closeAllNavGroups



	public linkClickHandler(navbarObject: NavGroup) {
		// A user clicked a link, end the hover on the parent group
		navbarObject.navGroupHoveredInCollapsedMode = false;
	}


	public toggleNavExtension() {
		// Tell the navbar to extend
		this.closeAllNavGroups();
		this.navbarService.updateNavbarState(!this.navBarExtended);
	}


	public ngOnInit() {
		this.navbarEventSubjectSubscription = this.navbarService.getNavbarStateAsObservable().subscribe((aState: NavbarState) => {
				this.navBarExtended = aState.isAppNavbarExtended;
			}
		)

		this.navbarAnimationSubjectSubscription = this.navbarService.getNavbarStateResizeEventAsObservable().subscribe((aState: NavbarState)=>{
			// When swapping back to extended mode from collapsed mode - automatically expand the current group
			if (!aState.isAppNavbarExtended) {
				this.handlePotentialNavSelectionHighlightingChange();
			}
		})

		// Check for page route changes that occurred without the navbar
		this.routerSubscription = this.router.events.pipe().subscribe((event) => {
			if (event instanceof NavigationEnd && !this.navBarExtended) {

				// The route changed, remove any nav highlighting
				this.flipAllOpenStatesToFalse();

				// Determine if the new route matches anything in the navbar
				this.handlePotentialNavSelectionHighlightingChange();
			}
		});

		// If the user navigated directly to a page by entering a link - we may need to highlight something
		this.handlePotentialNavSelectionHighlightingChange();
	}


	public ngOnDestroy(): void {
		// Unsubscribe from everything to prevent performance issues
		this.routerSubscription.unsubscribe();
		this.navbarEventSubjectSubscription.unsubscribe();
		this.navbarAnimationSubjectSubscription.unsubscribe();
	}

}
