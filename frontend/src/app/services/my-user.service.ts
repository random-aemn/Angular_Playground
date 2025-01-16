import { Injectable } from '@angular/core';
import {Observable, of} from "rxjs";
import {GridCellDataForRowSelectionDTO} from "../models/grid-cell-data-for-row-selection-dto";
import {
  GridPageWithCustomFilterComponent
} from "../grid-page-with-custom-filter/grid-page-with-custom-filter.component";
import {GridCellDataForCustomFilterDTO} from "../models/grid-cell-data-for-custom-filter-dto";

@Injectable({
  providedIn: 'root'
})
export class MyUserService {

  constructor() { }

  public getAllUsers2(): Observable<GridCellDataForCustomFilterDTO[]>{



    let retval: GridCellDataForCustomFilterDTO[] = [
      // object 1
      {
        id: 1,
        full_name: "Edward Teach",
        is_locked: true,
        is_locked_label: "Locked",
        registration_date: "06/07/2008",
        last_login_date: "09/09/2008",
      },
      // object 2
      {
        id: 24,
        full_name: "William Kidd",
        is_locked: true,
        is_locked_label: "Locked",
        registration_date: "01/08/2015",
        last_login_date: "09/30/2015",
      },
      // object 3
      {
        id: 33,
        full_name: "Calico Jack",
        is_locked: false,
        is_locked_label: "Unlocked",
        registration_date: "11/07/2018",
        last_login_date: "11/24/2024",
      }
    ]

    return of(retval);

  }


}
