import { Component } from '@angular/core';
import {ColDef, GridOptions} from "ag-grid-community";
import {RowDataDTO} from "../models/row-data-dto";

@Component({
  selector: 'app-reports-grid-view',
  templateUrl: './reports-grid-view.component.html',
  styleUrls: ['./reports-grid-view.component.scss']
})
export class ReportsGridViewComponent {

  public gridOptions: GridOptions = {
    domLayout: 'normal',
    debug: false,
    rowModelType: 'clientSide',
  }

  public columnDefs: ColDef[] = [
    {
      field: "id",
      headerName: "ID"

    },
    {
      field: "name",
      headerName: "Name"

    },
    {
      field: "priority",
      headerName: "Priority"

    },
    {
      field: "start_date",
      headerName: "Start Date"

    },
    {
      field: "end_date",
      headerName: "End Date"

    }
  ];

  // default column defs that are applied to every column so we don't have to add it every time - that's why these are not included in the columnDefs above
  public defaultColumnDef: ColDef = {
    flex: 1,
    sortable: true,
    resizable: true
  }

public rowData: RowDataDTO[] = [
  {
    id: 1,
    name: "Christopher",
    priority: "low",
    start_date: "12/17/2000",
    end_date: "1/1/2025"
  },
  {
    id: 2,
    name: "Annie",
    priority: "medium",
    start_date: "03/17/2000",
    end_date: "1/1/2027"

  },
  {
    id: 3,
    name: "Beauregard",
    priority: "HIGH",
    start_date: "10/15/2000",
    end_date: "1/1/2095"
  }
]

}
