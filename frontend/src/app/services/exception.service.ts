import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {GetExceptionDTO} from "../models/get-exception-dto";

@Injectable({
  providedIn: 'root'
})
export class ExceptionService {

  constructor(private httpClient: HttpClient) { }


  /*
   * Returns an observable that get a list of exceptions (with a filter applied)
   */
  public getListOfExceptions(aFilterNumber: number): Observable<GetExceptionDTO[]> {
    // Construct the URL to get the data to load the exceptions list grid
    const restUrl = environment.baseUrl + '/api/get-exceptions/' + aFilterNumber;

    // Return an observable that will hold a list of GetExceptionDTO objects
    return this.httpClient.get <GetExceptionDTO[]>(restUrl);
  }

}
