import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavbarService} from "../../../services/navbar.service";
import {NavbarState} from "../../../models/navbar-state";
import {NavGroup} from "../../../models/navbar/nav-group-dto";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {NavigationEnd, Router} from "@angular/router";
import {NavItem} from "../../../models/navbar/nav-item-dto";
import {delay, Subscription} from "rxjs";


@Component({
	selector: 'app-navbar-extended',
	templateUrl: './navbar-extended.component.html',
	styleUrls: ['./navbar-extended.component.scss'],
	animations: [
		trigger('expandCollapse', [
			state('expanded', style({height: '*', overflow: 'hidden'})),
			state('collapsed', style({height: '0', overflow: 'hidden'})),
			transition('expanded <=> collapsed', animate('200ms ease-in-out')),
		])
	]
})
export class NavbarExtendedComponent implements OnInit, OnDestroy {

	// Define the navbar groups, their items, and their properties in the shared navbarContents class
	public navBarObjects: NavGroup[];

	// Navbar states (for changing extended/collapsed modes)
	public navBarExtended: boolean;

	// These store the LAST/PREVIOUSLY opened nav item or group
	private openedNavItem: NavItem;
	private openedNavGroup: NavGroup;

	private routerSubscription: Subscription;
	private navbarAnimationSubjectSubscription: Subscription;
	private navbarEventSubjectSubscription: Subscription;

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

		if (!currentUrl.includes('404') && this.navBarExtended) {
			// Exclude 404 pages (no selection highlighting for 404 response)
			for (let navGroupObject of this.navBarObjects) {
				// Iterate through all the children of this object to see if the user is at the corresponding page
				for (let navChildObject of navGroupObject.navGroupItems) {
					if (currentUrl == ('/' + navChildObject.navItemUrl)) {
						if (navGroupObject.navGroupItems.length == 1) {
							// This is the only child of the parent group, so set the group to opened
							navGroupObject.navGroupOpenedState = true;
							this.openedNavGroup = navGroupObject;
						} else {
							// This is not the only child, so just set this child's state to opened
							navChildObject.navItemOpenedState = true;
							this.openedNavItem = navChildObject;
							// Open the parent group to show the user where they are on the navbar
							navGroupObject.showChildren = true;
						}
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

	private unsubscribeFromAll(): void {
		this.routerSubscription.unsubscribe();
		this.navbarEventSubjectSubscription.unsubscribe();
		this.navbarAnimationSubjectSubscription.unsubscribe();
	}

	public handleNavGroupClickEvent(navbarObject: NavGroup): boolean {

		// If there is only one child - route the user directly to it
		if (navbarObject.navGroupItems.length <= 1) {
			this.router.navigate([navbarObject?.navGroupItems[0].navItemUrl]);
		}

		if (navbarObject.navGroupItems.length > 1) {
			// There is more than 1 child element, so open up (or collapse) the list to show the user
			navbarObject.showChildren = !navbarObject.showChildren;
		}

		// PREVENT HREF NAVIGATION (DO NOT REMOVE THIS!!!)
		//		To allow right-clicking to create a new page - we've used an anchor tag with an href
		//		Returning false will prevent routing via href after they've double-clicked this
		return false;
	}  // End of handleNavGroupClickEvent

	public closeOtherNavGroupsAfterDoubleClick(navbarObject: NavGroup): boolean {
		// The user double-clicked this group when it was closed, open everything
		if (!navbarObject.showChildren) {
			for (let navObject of this.navBarObjects) {
				navObject.showChildren = true;
			}
		} else {
			// The user double-clicked this group when it was open, close everything
			for (let navObject of this.navBarObjects) {
				if (navObject.navGroupName != navbarObject.navGroupName) {
					navObject.showChildren = false;
				} else {
					// Keep the one that was clicked open
					navObject.showChildren = true;
				}
			}
		}

		// PREVENT HREF NAVIGATION (DO NOT REMOVE THIS!!!)
		//		To allow right-clicking to create a new page - we've used an anchor tag with an href
		//		Returning false will prevent routing via href after they've double-clicked this
		return false;
	}  // End of closeOtherNavGroupsAfterDoubleClick

	public toggleNavExtension() {
		this.closeAllNavGroups();
		this.navbarService.updateNavbarState(!this.navBarExtended);
	}

	public ngOnInit() {
		// This subscription fires the second the user presses the arrows in the navbar to change the state
		this.navbarEventSubjectSubscription = this.navbarService.getNavbarStateAsObservable().subscribe((aState: NavbarState) => {
				this.navBarExtended = aState.isAppNavbarExtended;
			}
		)

		// Check for page route changes that occurred to update selection highlighting in the navbar
		this.routerSubscription = this.router.events.pipe().subscribe((event) => {
			if (event instanceof NavigationEnd && this.navBarExtended) {

				// The route changed, remove any nav highlighting
				this.flipAllOpenStatesToFalse();

				// Determine if the new route matches anything in the navbar
				this.handlePotentialNavSelectionHighlightingChange();
			}
		});

		// This separate subscription is delayed to open the panel SLIGHTLY after the mode swaps
		this.navbarAnimationSubjectSubscription = this.navbarService.getNavbarStateAsObservable().pipe(delay(200)).subscribe((aState: NavbarState) => {
			// When swapping back to extended mode from collapsed mode - automatically expand the current group
			if (aState.isAppNavbarExtended) {
				this.handlePotentialNavSelectionHighlightingChange();
			}
		})

		// If the user navigated directly to a page by entering a link - we may need to highlight something
		this.handlePotentialNavSelectionHighlightingChange();
	}  // End of ngOnInit

	public ngOnDestroy(): void {
		// Unsubscribe from everything to avoid performance issues
		this.unsubscribeFromAll();
	}
}
