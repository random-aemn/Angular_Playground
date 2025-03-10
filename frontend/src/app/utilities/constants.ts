/*
 * This class holds the routes (that are visible to the user)
 *   1) If you wish to change the routes, then you *MUST* change the routes in the V1.1__security.sql
 *   2) Each route must start with 'page/' so that the backend knows to load the entire page (if a route is bookmarked)
 */
export enum Constants {
  FORBIDDEN_ROUTE                            = "page/403",

  SAMPLE_PAGE_ROUTE                          = "page/sample-page",
  SAMPLE_PAGE_ROUTE_2                        = "page/sample-page-2",
  USER_SETTINGS_ROUTE                        = "page/user-settings",
  LIST_EXCEPTIONS_ROUTE                      = "page/admin/list-exceptions",
  USER_ADMIN_ROUTE                           = "page/admin/user-admin",
  EXERCISE_1A_ROUTE                          = "page/exercise-1a",
  EXERCISE_1B_ROUTE                          = "page/exercise1b",
  EXERCISE_1C_ROUTE                          = "page/registration/approved",
  EXERCISE_1D_ROUTE                          = "page/html-over-image",
  EXERCISE_2_MY_Settings_ROUTE               = "page/my-settings",
  EXERCISE_3_STOCK_TRADES                    = "page/stock-trades",
  EXERCISE_4_RESPONSIVE_LAYOUT               = "page/responsive-layout",
  EXERCISE_5_VARIABLE_HEIGHT                 = "page/variable-height",
  EXERCISE_5B_HOLY_GRAIL                     = "page/holy-grail-not",
  EXERCISE_5B_HOLY_GRAIL_REAL                = "page/holy-grail-real",
  EXERCISE_5C_SCROLL_INTO_VIEW                = "page/scroll-into-view",
  EXERCISE_6B_SLIDE_OUT_DRAWER                = "page/slide-out-help",
  EXERCISE_13A_OBSERVABLE_DROPDOWN                 = "page/add-contract-subscribe",
  EXERCISE_21b_ACKNOWLEDGEMENT_PAGE                = "page/acknowledgement",
  EXERCISE_5_TWO_HEIGHT                        = "page/variable-height-two",
  EXERCISE_6_B_2                               = "page/slide-out-help-2",
  EXERCISE_6_C                               = "page/smooth-vert-transition-1",
  EXERCISE_10_B                               = "page/edit-contract-spec",
  EXERCISE_11_A                              = "page/reports/grid-v:",
  EXERCISE_11_B                              = "page/reports/my-searches",
  EXERCISE_11_C                              = "page/grid-page-with-filters",
  EXERCISE_11_H                              = "page/grid-page-with-row-selection",
  EXERCISE_11_I                              = "page/grid-page-with-custom-filter",
  EXERCISE_12_A                              = "page/tab-group-page",
  EXERCISE_12_B                              = "page/pretty-tabs",
  EXERCISE_SWITCH_TABS                       = "page/switch-tabs",
  EXERCISE_25A                               = "page/dashboard",
  EXERCISE_25C                               = "page/dashboard/pie-chart",
  EXERCISE_26A                               = "page/chips-with-textbox",
  EXERCISE_14D                               = "page/autocomplete-textbox",
  EXERCISE_26B                               = "page/chips-with-autocomplete",
  EXERCISE_26C                               = "page/grid-page-with-chip-selection",

  // Preference Names
  NAVBAR_EXTENDED_STATE_PREFERENCE_NAME      = "navbar_extended_state", // Preference name for navbar mode (extended/collapsed)
  HEADER_THEME_PREFERENCE_NAME               = "header_theme_state",
  COLUMN_STATE_PREFERENCE_NAME               = "column_state",
  EXCEPTION_FILTER_PREFERENCE_NAME           = "exceptions_filter",
}
