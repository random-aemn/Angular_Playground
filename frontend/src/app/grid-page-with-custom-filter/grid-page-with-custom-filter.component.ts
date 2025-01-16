import { Component } from '@angular/core';
import {ColDef, ColumnApi, GridApi, GridOptions, GridReadyEvent, ITextFilterParams} from "ag-grid-community";
import {GridCellDataForRowSelectionDTO} from "../models/grid-cell-data-for-row-selection-dto";
import {MyReportService} from "../services/my-report.service";
import {DateService} from "../services/date.service";
import {MyUserService} from "../services/my-user.service";
import {GridCellDataForCustomFilterDTO} from "../models/grid-cell-data-for-custom-filter-dto";

@Component({
  selector: 'app-grid-page-with-custom-filter',
  templateUrl: './grid-page-with-custom-filter.component.html',
  styleUrls: ['./grid-page-with-custom-filter.component.scss']
})
export class GridPageWithCustomFilterComponent {

  constructor(private myUserService: MyUserService,
              private dateService: DateService,) {
  }

  gridApi: GridApi;
  gridColumnApi: ColumnApi;
  totalRecords: number;

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
      field: "is_locked",
      headerName: "Is Locked"
    },
    {
      field: "is_locked_label",
      headerName: "Is Locked Label",

    },
    {
      field: "registration_date",
      headerName: "Registration Date",
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
    this.myUserService.getAllUsers2().subscribe((aData: GridCellDataForCustomFilterDTO[]) => {
      this.gridApi.setRowData(aData);
      this.totalRecords = aData.length;
    })
  }


}
