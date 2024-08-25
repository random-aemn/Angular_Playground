import {NavItem} from "./nav-item-dto";

export class NavGroup {
	/**
	 * @param faIconTag - The tag-name of the FontAwesome icon for the group.
	 * @param navGroupName - The name of the navigation group that the user sees.
	 * @param navGroupIndex - The position of the navigation group in the navbar.
	 * @param navGroupItems - The array of navigation items in the group.
	 * @param showChildren - The state determining if the child elements are shown in the navbar (when it's extended).
	 * @param showChildren - The state of the item (true if open, false if closed).
	 * @param navGroupHoveredInCollapsedMode - The hover state of the item ONLY USED IN COLLAPSED MODE (true = open).
	 */
	public faIconTag: string;
	public navGroupName: string;
	public navGroupItems: NavItem[];
	public navGroupIndex?: number = 0;
	public showChildren?: boolean = false;
	public navGroupOpenedState?: boolean = false;
	public navGroupHoveredInCollapsedMode?: boolean = false;
}
