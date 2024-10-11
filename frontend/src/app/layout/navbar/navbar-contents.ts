// navbarItems.ts
import {Constants} from "../../utilities/constants";
import {NavGroup} from "../../models/navbar/nav-group-dto";


/*
	Developer notes:
	- This class contains all the nav items for the entire app - this is the only place you should be adding nav items.
	- When copying items, be sure to include the maps - they are there to automatically set values to their defaults.
	- Changes made to this file will impact BOTH the collapsed and extended navbar components - test BOTH of them!
	- When adding nav-items, you should not need to touch any HTML
	- TWO NAV ELEMENTS SHOULD NEVER SHARE A URL/ADDRESS!! (this will break the selection highlighting functions)

	Remember:
	- Copying is easier/safer when things are collapsed
 */

export const navbarContents: NavGroup[] = [
	{
		navGroupName: "Group #1",
		faIconTag: "fa-table-layout",
		navGroupItems: [
			{
				navItemName: "Sample Page",
				navItemUrl: Constants.SAMPLE_PAGE_ROUTE,
			},
			{
				navItemName: "Sample Page 2",
				navItemUrl: Constants.SAMPLE_PAGE_ROUTE_2,
			}
		],
	},


	{
		navGroupName: "Administration",
		faIconTag: "fa-file-chart-column",
		navGroupItems: [
      {
        navItemName: "List Exceptions",
        navItemUrl: Constants.LIST_EXCEPTIONS_ROUTE,
      },
      {
        navItemName: "User Admin2",
        navItemUrl: Constants.USER_ADMIN_ROUTE,
      },
		]
	},

  {
		navGroupName: "Class Exercises",
		faIconTag: "fa-sharp-duotone fa-solid fa-signs-post",
		navGroupItems: [
      {
        navItemName: "Exercise Page 1",
        navItemUrl: Constants.EXERCISE_1A_ROUTE,
      },
      {
        navItemName: "Exercise Page 1b",
        navItemUrl: Constants.EXERCISE_1B_ROUTE,
      },
      {
        navItemName: "Exercise Page 1C / Registration Approved",
        navItemUrl: Constants.EXERCISE_1C_ROUTE,
      },
      {
        navItemName: "Exercise Page 1D / HTML over Image",
        navItemUrl: Constants.EXERCISE_1D_ROUTE,
      },
      {
        navItemName: "Exercise Page 2 / My Settings",
        navItemUrl: Constants.EXERCISE_2_MY_Settings_ROUTE,
      },
      {
        navItemName: "Exercise Page 3a / Stock Trade",
        navItemUrl: Constants.EXERCISE_3_STOCK_TRADES,
      },
      {
        navItemName: "Exercise Page 4 / Responsive Layout",
        navItemUrl: Constants.EXERCISE_4_RESPONSIVE_LAYOUT,
      },
      {
        navItemName: "Exercise Page 5 / Variable Height",
        navItemUrl: Constants.EXERCISE_5_VARIABLE_HEIGHT,
      }
		]
	},


	{
		// !!!!!THIS HAS TO BE THE LAST ITEM IN THE CLASS!!!!!
		// If you rename this nav group, you'll need to update the HTML in BOTH navbar components (extended & collapsed)
		navGroupName: "John Doe",
		faIconTag: "fa-circle-user",
		navGroupItems: [
			{
				// If you add more than one item here, you'll want to edit the HTML to expand UPWARDS! (Ask Alex Please)
				navItemName: "User Settings",
				navItemUrl: Constants.USER_SETTINGS_ROUTE,
			},
		]
	}
]
