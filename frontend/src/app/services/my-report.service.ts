import { Injectable } from '@angular/core';
import {GridCellDataForRowSelectionDTO} from "../models/grid-cell-data-for-row-selection-dto";
import {Observable, of} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MyReportService {

  constructor() { }

  public getAllUsers(): Observable<GridCellDataForRowSelectionDTO[]>{



    let retval: GridCellDataForRowSelectionDTO[] = [
      // object 1
      {
        id: 1,
        full_name: "George Washington",
        csv_roles: "President 1",
        account_created_date: "04/30/1789",
        last_login_date: "12/14/1799"
      },
      // object 2
      {
        id: 2,
        full_name: "Zachary Taylor",
        csv_roles: "President 12",
        account_created_date: "03/15/1849",
        last_login_date: "07/07/1850"
      },
      // object 3
      {
        id: 3,
        full_name: "Jimmy Carter",
        csv_roles: "President 39",
        account_created_date: "01/20/1977",
        last_login_date: "12/29/2024"
      }
    ]

    return of(retval);

  }

}
