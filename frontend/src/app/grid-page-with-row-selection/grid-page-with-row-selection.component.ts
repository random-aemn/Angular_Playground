import { Component } from '@angular/core';
import {ColDef, Column, ColumnApi, GridApi, GridOptions, GridReadyEvent, ITextFilterParams} from "ag-grid-community";
import {MyReportService} from "../services/my-report.service";
import {GridCellDataForRowSelectionDTO} from "../models/grid-cell-data-for-row-selection-dto";
import {DateService} from "../services/date.service";

@Component({
  selector: 'app-grid-page-with-row-selection',
  templateUrl: './grid-page-with-row-selection.component.html',
  styleUrls: ['./grid-page-with-row-selection.component.scss']
})
export class GridPageWithRowSelectionComponent {

  constructor(private myReportService: MyReportService,
              private  dateService: DateService) {
  }

  gridApi: GridApi;
  gridColumnApi: ColumnApi;

  public gridOptions: GridOptions = {
    domLayout: 'normal',
    debug: false,
    rowModelType: 'clientSide',
    rowSelection: 'multiple',
    suppressCellFocus: true,
  };

  public columnDefs: ColDef[] = [
    {
      field: "id",
      headerName: "ID",
      checkboxSelection: true,
    },
    {
      field: "full_name",
      headerName: "Full Name"
    },
    {
      field: "csv_roles",
      headerName: "CSV Roles"
    },
    {
      field: "account_created_date",
      headerName: "Account Created Date",
      comparator: (a: string, b: string) => this.dateService.dateComparator(a,b)

    },
    {
      field: "last_login_date",
      headerName: "Last Login Date",
      comparator: (a: string, b: string) => this.dateService.dateComparator(a,b)

    },
  ]

  private textFilterParams: ITextFilterParams = {
    filterOptions: ['contains', 'notContains'],
    caseSensitive: false,
    debounceMs: 200,
  }


  public defaultColumnDefs: ColDef = {
    flex: 1,
    sortable: true,
    resizable: true,
    filter: 'agTextColumnFilter',
    floatingFilter: true,
    filterParams: this.textFilterParams
  }

  public onGridReady(aParams: GridReadyEvent){
    this.gridApi = aParams.api;
    this.gridColumnApi = aParams.columnApi;
    this.gridApi.showLoadingOverlay();
    this.myReportService.getAllUsers().subscribe((aData: GridCellDataForRowSelectionDTO[]) => {
      this.gridApi.setRowData(aData);
    })
  }



}
