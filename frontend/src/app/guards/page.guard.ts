import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot} from '@angular/router';
import {inject, Injectable} from "@angular/core";
import {UserService} from "../services/user.service";
import {map, Observable} from "rxjs";
import {UserInfoDTO} from "../models/user-info-dto";
import {Constants} from "../utilities/constants";

export namespace PageGuard {

  @Injectable({
    providedIn: 'root'
  })
  class InternalPageGuardService {

    constructor(private router: Router,
                private userService: UserService) {}


    public canActivate(aActivatedRouteSnapshot: ActivatedRouteSnapshot): Observable<boolean> {

      return this.userService.getLoggedInUserInfo().pipe(
        map((userInfoDTO: UserInfoDTO) => {
          // I got the UserInfoDTO from the back-end (or cache)

          // Get the next url from the routeConfig
          let nextUrl: string | undefined = aActivatedRouteSnapshot.routeConfig?.path;
          if (!nextUrl) {
            return false;
          }

          // Check if the url is allowed
          let routeAllowed: boolean | undefined = userInfoDTO.pageRoutes.get(nextUrl);

          if (!routeAllowed) {
            this.router.navigate([Constants.FORBIDDEN_ROUTE]).then();

            // Return false so that the router will *NOT* proceed with the route
            // -- Instead the user is routed to the FORBIDDEN page
            return false;
          }

          // Return true so the router will allow the route change to proceed.
          return true;
        }));

    } // end of canActivate()

  }  // end of InternalPageGuardService


  /*
   * Problem:    Angular is deprecating the CanActivate interface
   * Solution:   if you have older guards, then you can still use them
   *             Convert your older guard into a service and inject it below
   */
  export const canActivate: CanActivateFn = (aActivatedRouteSnapshot: ActivatedRouteSnapshot, aRouterStateSnapshot: RouterStateSnapshot) => {
    // Inject the InternalPageGuardService and call canActivate()
    return inject(InternalPageGuardService).canActivate(aActivatedRouteSnapshot);
  };

}
