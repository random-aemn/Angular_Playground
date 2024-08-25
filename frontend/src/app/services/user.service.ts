import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, EMPTY, map, Observable, shareReplay} from "rxjs";
import {environment} from "../../environments/environment";
import {UserInfoDTO} from "../models/user-info-dto";


@Injectable({
  providedIn: 'root'
})
export class UserService {

  // The cache holds key=type value=cached-observable
  private cache: any = {};

  private readonly loggedInUserInfoKey: string = "u";


  public constructor(private httpClient: HttpClient) {
  }


  /*
  * Return an observable holding the UserInfoDTO object
  * NOTE:  First time this is called, the REST endpoint is invoked
  *        Second time this is called, the cached observable is returned
  */
  public getLoggedInUserInfo(): Observable<UserInfoDTO> {
    const key = this.loggedInUserInfoKey;

    if (this.cache[key]) {
      // This observable is in the cache.  So, return it from the cache
      return this.cache[key];
    }


    // Construct the URL to get basic information about the user
    const restUrl: string = environment.baseUrl + '/api/user/me';

    // Setup this observable so that it calls shareReplay(1) to replay the previous value
    this.cache[key] = this.httpClient.get <UserInfoDTO>(restUrl).pipe(
      map((userInfoDTO: UserInfoDTO) => {

        // Convert the userInfoDTO.pageRoutes into a map
        // So that the PageGuard does not have to do it repeatedly
        let mapPageRoutes: Map<string, boolean> = new Map(Object.entries(userInfoDTO.pageRoutes));

        userInfoDTO.pageRoutes = mapPageRoutes;
        return userInfoDTO;
      }),
      shareReplay(1),
      catchError(err => {
        console.error('There was an error in getLoggedInUserInfo()  Error is ', err);

        // Clear the cache
        delete this.cache[key];

        return EMPTY;
      }));

    // Return the observable
    return this.cache[key];
  }


}
