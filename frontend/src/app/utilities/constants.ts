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

  // Preference Names
  NAVBAR_EXTENDED_STATE_PREFERENCE_NAME      = "navbar_extended_state", // Preference name for navbar mode (extended/collapsed)
  HEADER_THEME_PREFERENCE_NAME               = "header_theme_state",

}
