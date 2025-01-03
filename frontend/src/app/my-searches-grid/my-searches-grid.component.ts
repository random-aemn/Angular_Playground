import { Component } from '@angular/core';
import {ColDef, ColumnApi, GridApi, GridOptions, GridReadyEvent} from "ag-grid-community";
import {MySearchService} from "../services/my-search.service";
import {SavedSearchDTO} from "../models/saved-search-dto";

@Component({
  selector: 'app-my-searches-grid',
  templateUrl: './my-searches-grid.component.html',
  styleUrls: ['./my-searches-grid.component.scss']
})
export class MySearchesGridComponent {

  public gridApi: GridApi;
  public gridColumnApi: ColumnApi;

  constructor(private mySearchService: MySearchService,) {
  }


  public gridOptions: GridOptions = {
    domLayout: 'normal',
    debug: false,
    rowModelType: 'clientSide'
  }

  public columnDefs: ColDef[] = [
    {
      field: "id",
      headerName: "ID",
      hide: true,
    },
    {
      field: "name",
      headerName: "Name",
      minWidth: 250,
    },
    {
      field: "search_query",
      headerName: "Search Query"
    },
    {
      field: "last_executed_date",
      headerName: "Last Executed Date"
    }

  ]

  public defaultColumnDef: ColDef = {
    flex: 1,
    sortable: true,
    resizable: true,

  }

  public onGridReady(aParams: GridReadyEvent) {
    this.gridApi = aParams.api;
    this.gridColumnApi = aParams.columnApi;

    this.gridApi.showLoadingOverlay();
    this.mySearchService.getUsersSavedSearches().subscribe((aData: SavedSearchDTO[]) => {
    //   The REST call came back and we got the data
      this.gridApi.setRowData(aData);
    })


  }


}
