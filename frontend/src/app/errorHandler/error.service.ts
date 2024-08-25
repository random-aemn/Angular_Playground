import { Injectable } from '@angular/core';
import {HttpErrorResponse} from "@angular/common/http";
import {Observable, Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  private errors = new Subject<HttpErrorResponse>();

  constructor() { }


  public addError(aError: HttpErrorResponse): void {
    this.errors.next(aError);
  }


  public getErrorsAsObservable(): Observable<HttpErrorResponse> {
    return this.errors.asObservable();
  }

}
