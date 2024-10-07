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

  // Preference Names
  NAVBAR_EXTENDED_STATE_PREFERENCE_NAME      = "navbar_extended_state", // Preference name for navbar mode (extended/collapsed)
  HEADER_THEME_PREFERENCE_NAME               = "header_theme_state",
  COLUMN_STATE_PREFERENCE_NAME               = "column_state",
  EXCEPTION_FILTER_PREFERENCE_NAME           = "exceptions_filter",
}
