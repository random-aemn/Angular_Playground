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
      },
      {
        navItemName: "Exercise Page 5b / Fake Holy Grail",
        navItemUrl: Constants.EXERCISE_5B_HOLY_GRAIL,
      },
      {
        navItemName: "Exercise Page 5b / Real Holy Grail",
        navItemUrl: Constants.EXERCISE_5B_HOLY_GRAIL_REAL,
      },
      {
        navItemName: "Exercise Page 5c / Scroll into view",
        navItemUrl: Constants.EXERCISE_5C_SCROLL_INTO_VIEW,
      },
      {
        navItemName: "Exercise Page 6B / Slide Out Help",
        navItemUrl: Constants.EXERCISE_6B_SLIDE_OUT_DRAWER,
      },
      {
        navItemName: "Exercise Page 13A / Dropdown Observable",
        navItemUrl: Constants.EXERCISE_13A_OBSERVABLE_DROPDOWN,
      },
      {
        navItemName: "5-Two",
        navItemUrl: Constants.EXERCISE_5_TWO_HEIGHT,
      },
      {
        navItemName: "Slide Out Help",
        navItemUrl: Constants.EXERCISE_6_B_2,
      },
      {
        navItemName: "Vertical Transitions 1",
        navItemUrl: Constants.EXERCISE_6_C,
      },
      {
        navItemName: "Date Picker!",
        navItemUrl: Constants.EXERCISE_10_B,
      },
      {
        navItemName: "AG-GRID",
        navItemUrl: Constants.EXERCISE_11_A,
      },
      {
        navItemName: "Searches Grid",
        navItemUrl: Constants.EXERCISE_11_B,
      },
      {
        navItemName: "Grid with Filters",
        navItemUrl: Constants.EXERCISE_11_C,
      },
      {
        navItemName: "Grid with Selection",
        navItemUrl: Constants.EXERCISE_11_H,
      },
      {
        navItemName: "Grid with custom filter",
        navItemUrl: Constants.EXERCISE_11_I,
      },
      {
        navItemName: "Tab Group",
        navItemUrl: Constants.EXERCISE_12_A,
      },
      {
        navItemName: "Pretty Tabs",
        navItemUrl: Constants.EXERCISE_12_B,
      },
      {
        navItemName: "Switch Tabs",
        navItemUrl: Constants.EXERCISE_SWITCH_TABS,
      },
      {
        navItemName: "Highcharts Dashboard",
        navItemUrl: Constants.EXERCISE_25A,
      },
      {
        navItemName: "Chips with Textbox",
        navItemUrl: Constants.EXERCISE_26A,
      },

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
