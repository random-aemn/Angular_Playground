export class NavItem {
	/**
	 * @param navItemName - The name of the navigation item that the user sees.
	 * @param navItemUrl - The URL associated with the navigation item.
	 * @param navItemIndex - The position of the navigation item in the list.
	 * @param navItemOpenedState - The state of the item (true if open, false if closed).
	 */
	public navItemName: string;
	public navItemUrl: string;
	public navItemIndex?: number = 0;  // This is automatically set in the object map - it IS used
	public navItemOpenedState?: boolean = false;  // This is automatically set in the object map - it IS used
}
