import { Injectable } from '@angular/core';
import {Observable, of} from "rxjs";
import {MyLookupDTO} from "../models/MyLookupDTO";

@Injectable({
  providedIn: 'root'
})
export class MyLookupService {


  constructor() { }

  public getAllPriorities(): Observable<MyLookupDTO[]>{

    let data: MyLookupDTO[] = [
      {
        id: 1,
        value: 'Low'
      },
      {
        id: 2,
        value: 'Medium'
      },
      {
        id: 3,
        value: 'High'
      },
      {
        id: 4,
        value: 'Critical'
      },
    ];

return of(data)
  }
}
