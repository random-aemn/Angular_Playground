import { Component } from '@angular/core';
import {ColDef, GridOptions, ITextFilterParams} from "ag-grid-community";

@Component({
  selector: 'app-grid-page-with-filters',
  templateUrl: './grid-page-with-filters.component.html',
  styleUrls: ['./grid-page-with-filters.component.scss']
})
export class GridPageWithFiltersComponent {

  public gridOptions: GridOptions = {
    domLayout: 'normal',
    debug: false,
    rowModelType: 'clientSide',
  };

  private textFilterParams: ITextFilterParams = {
    filterOptions: ['contains', 'notContains'],
    caseSensitive: false,
    debounceMs: 200,
  }

  public columnDefs: ColDef[] = [
    {
   field: "id",
   headerName: "ID"
   },
   {
   field: "report_name",
   headerName: "Report Name"
   },
   {
   field: "indicator_count",
   headerName: "Indicator Count"
   },
   {
   field: "last_updated_date",
   headerName: "Last Updated Date"
   }
  ]

  public defaultColumnDef: ColDef = {
    flex: 1,
    sortable: true,
    resizable: true,
    filter: 'agTextColumnFilter',
  floatingFilter: true,
  filterParams: this.textFilterParams
  }

}
