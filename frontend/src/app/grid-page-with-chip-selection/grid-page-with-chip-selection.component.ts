import { Component } from '@angular/core';
import {
  ColDef,
  ColumnApi,
  GridApi,
  GridOptions,
  GridReadyEvent,
  ITextFilterParams, RowDoubleClickedEvent,
  TextFilterParams
} from "ag-grid-community";
import {MyContractService} from "../services/my-contract.service";
import {GridWithChipSelectionDTO} from "../models/grid-with-chip-selection-dto";
import {SelectedReviewerDTO} from "../models/selected-reviewer-dto";

@Component({
  selector: 'app-grid-page-with-chip-selection',
  templateUrl: './grid-page-with-chip-selection.component.html',
  styleUrls: ['./grid-page-with-chip-selection.component.scss']
})
export class GridPageWithChipSelectionComponent {

  constructor(private myContractService: MyContractService) {
  }

  public gridApi: GridApi;
  public gridColumnApi: ColumnApi;

  public selectedReviewers : SelectedReviewerDTO[] = [];

  public addReviewer(aReviewer: GridWithChipSelectionDTO){

    this.myContractService.addContractReviewer(aReviewer.user_id).subscribe( () => {

      let newReviewer: SelectedReviewerDTO = {
        user_id: aReviewer.user_id,
        full_name: aReviewer.full_name,
        remove_in_progress: false
      };

      // Add this reviewer to the array of reviewers
      this.selectedReviewers.push(newReviewer);

    });

  }

  public removeReviewer(aIndexOfSelectedReviewer: number): void {
    let selectedReviewer: SelectedReviewerDTO = this.selectedReviewers[aIndexOfSelectedReviewer];

    this.myContractService.removeContractReviewer(selectedReviewer.user_id).subscribe( () => {
      //   if the REST call succeeded, remove the reviewer from the array
      this.selectedReviewers.splice(aIndexOfSelectedReviewer, 1);

    });

  }

  public onGridReady(aParams: GridReadyEvent){
    this.gridApi = aParams.api;
    this.gridColumnApi = aParams.columnApi;
    this.gridApi.showLoadingOverlay();
    this.myContractService.getContractReviewers().subscribe((aData:GridWithChipSelectionDTO[]) => {
      this.gridApi.setRowData(aData);
    })

  }


  public gridOptions: GridOptions = {
    domLayout: 'normal',        // requires the wrapper div to have a height set *OR* a class="h-full" on it
    rowModelType: 'clientSide',
    onRowDoubleClicked: (params: RowDoubleClickedEvent) => {
      this.addReviewer(params.data);
    }
  }

  public columnDefs: ColDef[] = [
    {
      field: "user_id",
      headerName: "User Id"
    },
    {
      field: "full_name",
      headerName: "Full Name"
    },
    {
      field: "title",
      headerName: "Title"
    },
    {
      field: "primary_org",
      headerName: "Primary Org"
    },
    {
      field: "secondary_org",
      headerName: "Secondary Org"
    },
  ]


  // Customize the filters (when turned on)
  private textFilterParams: ITextFilterParams = {
    filterOptions: ['contains', 'notContains'],         // Customize the filter to only show "Contains" and "Not Contains"
    caseSensitive: false,                               // Filter is case-insensitive
    debounceMs: 200,
    maxNumConditions: 1                                 // Suppress the And/Or
  };

  public defaultColumnDef: ColDef = {
    flex: 1,
    sortable: true,
    resizable: true,
    filter: 'agTextColumnFilter',
    floatingFilter: true,
    filterParams: this.textFilterParams,
  }





}
