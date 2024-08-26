import {Component, HostListener, OnDestroy, OnInit} from '@angular/core';
import {ColDef, ColumnApi, ColumnState, GridApi, GridOptions, GridReadyEvent, ICellRendererParams, RowDoubleClickedEvent} from "ag-grid-community";
import {debounceTime, Observable, Subject, Subscription, switchMap} from "rxjs";
import {NavbarService} from "../../../../services/navbar.service";
import {PreferenceService} from "../../../../services/preference.service";
import {Constants} from "../../../../utilities/constants";
import {GetOnePreferenceDTO} from "../../../../models/preferences/get-one-preference-dto";
import {ListExceptionsActionRendererComponent} from "../list-exceptions-action-renderer/list-exceptions-action-renderer.component";
import {BannerService} from "../../../../services/banner.service";
import {ExceptionService} from "../../../../services/exception.service";
import {GetExceptionDTO} from "../../../../models/get-exception-dto";
import {DateService} from "../../../../services/date.service";

@Component({
  selector: 'app-list-exceptions-grid',
  templateUrl: './list-exceptions-grid.component.html',
  styleUrls: ['./list-exceptions-grid.component.scss']
})
export class ListExceptionsGridComponent implements OnInit, OnDestroy {
  public  gridApi:                          GridApi;
  public  gridColumnApi:                    ColumnApi;
  private resizeSubject:                    Subject<any> =   new Subject<Event>();
  private navbarSubscription:               Subscription;
  private resizeSubscription:               Subscription;
  private saveGridEventsSubscription:       Subscription;
  public  selectedRow:                      GetExceptionDTO = new GetExceptionDTO();
  public  isSideBarVisible:                 boolean = false;
  private listenForGridChanges:             boolean = false;
  private userHasPastColumnState:           boolean = false;
  private saveGridColumnStateEventsSubject: Subject<ColumnState[]> = new Subject();

  public  readonly LAST_ONE_DAY_FILTER:     number = 1;
  public  readonly LAST_SEVEN_DAY_FILTER:   number = 2;
  public  readonly LAST_THIRTY_DAY_FILTER:  number = 3;
  public  readonly YEAR_TO_DATE_FILTER:     number = 4;
  public  readonly SHOW_ALL_DATA_FILTER:    number = 5;

  private preferencesPageName:              string  = "list-exceptions-page";
  private readonly DEFAULT_FILTER:          number  = this.LAST_THIRTY_DAY_FILTER;

  public selectedFilter:                    number = this.DEFAULT_FILTER;
  public selectedFilterLabel:               string;
  public bannerHeightInPixelsObs: Observable<string>;

  constructor(private exceptionService:   ExceptionService,
              private bannerService:      BannerService,
              private navbarService:      NavbarService,
              private preferenceService:  PreferenceService,
              private dateService:        DateService) {

    // Use this to prevent grid reset spamming during page resize events
    this.resizeSubscription = this.resizeSubject.pipe(debounceTime(150)).subscribe(() => {
      // This code will only run once every 150ms (during page resizing)
      if (this.gridApi) {
        this.gridApi.sizeColumnsToFit();
      }
    });
  }


  private textFilterParams = {
    filterOptions: ['contains', 'notContains'],
    caseSensitive: false,
    debounceMs: 200,
    maxNumConditions: 1,           // suppress and/or
  };




  public gridOptions: GridOptions = {
    domLayout: 'normal',  // Requires the wrapper div to have a height set *OR* a class="h-full" on it
    debug: false,
    rowModelType: 'clientSide',
    suppressCellFocus: true,
    suppressRowTransform: true,
    suppressRowHoverHighlight: false,
    suppressContextMenu: true,

    onRowDoubleClicked:(event: RowDoubleClickedEvent) => {
      this.openSideBar(event.data)
    },

    onSortChanged: () => {
      this.saveColumnState();
    },

    onDragStopped: () => {
      this.saveColumnState();
    },

    onDisplayedColumnsChanged: () => {
      this.saveColumnState();
    },

    onColumnVisible: () => {
      this.saveColumnState();
    },

    onColumnPinned: () => {
      this.saveColumnState();
    },

  }

  public defaultColDefs: ColDef = {
    sortable: true,
    resizable: true,
    floatingFilter: true,	// Causes the filter row to appear below column names
    filter: 'agTextColumnFilter',
    filterParams: this.textFilterParams,
  };

  public columnDefs: ColDef[] = [
    {
      headerName: 'Action',
      maxWidth: 90,
      field: 'id',
      filter: false,
      suppressMenu: true,
      resizable: false,
      sortable: false,
      cellRenderer: ListExceptionsActionRendererComponent,
      cellRendererParams: {
        viewDetailsClicked: (params: ICellRendererParams) => this.viewDetailsClicked(params)
      }
    },
    {
      field: 'id',
      headerName: 'Id',
      cellClass: 'grid-text-cell-format',
      filter: 'agNumberColumnFilter',
      filterParams: this.textFilterParams,
    },
    {
      field: 'app_name',
      headerName: 'App Name',
      cellClass: 'grid-text-cell-format',
      filter: 'agTextColumnFilter',
      filterParams: this.textFilterParams,
    },
    {
      field: 'app_version',
      headerName: 'Version',
      cellClass: 'grid-text-cell-format',
      filter: 'agTextColumnFilter',
      filterParams: this.textFilterParams,
    },
    {
      field: 'event_date',
      headerName: 'Event Date',
      cellClass: 'grid-text-cell-format',
      comparator: (a: string, b: string) => this.dateService.dateComparator(a,b)
    },
    {
      field: 'message',
      headerName: 'Message',
      cellClass: 'grid-text-cell-format',
      filter: 'agTextColumnFilter',
      filterParams: this.textFilterParams
    },
    {
      field: 'url',
      headerName: 'URL',
      cellClass: 'grid-text-cell-format',
      filter: 'agTextColumnFilter',
      filterParams: this.textFilterParams
    },
    {
      field: 'user_cert_name',
      headerName: 'User Certificate',
      cellClass: 'grid-text-cell-format',
      filter: 'agTextColumnFilter',
      filterParams: this.textFilterParams
    },
    {
      field: 'user_full_name',
      headerName: 'User Full Name',
      cellClass: 'grid-text-cell-format',
      filter: 'agTextColumnFilter',
      filterParams: this.textFilterParams,
      hide: true
    },
    {
      field: 'cause',
      headerName: 'Cause',
      cellClass: 'grid-text-cell-format',
      filter: 'agTextColumnFilter',
      filterParams: this.textFilterParams,
      hide: true
    }
  ];



  public ngOnInit(): void {
    this.bannerHeightInPixelsObs = this.bannerService.getHeightInPixelsObs();

    // Subscribe to the navbarService to resize the grid whenever the navbar resizes
    this.navbarSubscription = this.navbarService.getNavbarStateResizeEventAsObservable().subscribe(()=>{
      // Code inside this block will execute any time the navbar resizes AND on page load

      if (! this.userHasPastColumnState) {
        // Resize the grid
        this.gridApi.sizeColumnsToFit();
      }
    })

    // Listen for save-grid-column-state events
    // NOTE:  If a user manipulates the grid, then we could be sending LOTS of save-column-state REST calls
    //        The debounceTime slows down the REST calls
    //        The switchMap cancels previous calls
    //        Thus, if there are lots of changes to the grid, we invoke a single REST call using the *LAST* event (over a span of 250 msecs)
    this.saveGridEventsSubscription = this.saveGridColumnStateEventsSubject.asObservable().pipe(
      debounceTime(250),         // Wait 250 msecs before invoking REST call
      switchMap( (aNewColumnState: any) => {
        // Use the switchMap for its cancelling effect:
        // On each observable, the previous observable is cancelled
        // Return an observable

        this.userHasPastColumnState = true;

        // Invoke the REST call to save it to the back end
        return this.preferenceService.setPreferenceValueForPageUsingJson(Constants.COLUMN_STATE_PREFERENCE_NAME, aNewColumnState, this.preferencesPageName)
      })
    ).subscribe();
  }


  public ngOnDestroy(): void {
    if (this.navbarSubscription) {
      this.navbarSubscription.unsubscribe();
    }

    if (this.resizeSubscription) {
      this.resizeSubscription.unsubscribe();
    }

    if (this.saveGridEventsSubscription) {
      this.saveGridEventsSubscription.unsubscribe();
    }
  }


  private clearGridSorting(): void {
    this.gridColumnApi.applyColumnState({
      defaultState: {
        sort: null
      }
    });
  }

  private saveColumnState(): void {
    if (this.listenForGridChanges) {
      // The grid has rendered data.  So, save the sort/column changes

      // Get the current column state
      let currentColumnState = this.gridColumnApi.getColumnState();

      // Send a message to save the current column state
      this.saveGridColumnStateEventsSubject.next(currentColumnState)
    }
  }



  public firstDataRendered(): void {
    // The grid is fully rendered.  So, set the flag to start saving sort/column changes
    this.listenForGridChanges = true;
  }

  public resetGrid(): void {
    // Reset the columns before sizing them
    this.gridColumnApi.resetColumnState();
    this.gridApi.sizeColumnsToFit();

    // Clear all the grid sorting
    this.clearGridSorting();

    // Clear all the filters
    this.gridApi.setFilterModel(null);
  }




  public onGridReady(aParams: GridReadyEvent) {
    // Get a reference to the gridApi and gridColumnApi (which we will need later to get selected rows)
    this.gridApi = aParams.api;
    this.gridColumnApi = aParams.columnApi;

    // Show the loading overlay
    this.gridApi.showLoadingOverlay();

    // Get the preferences for the grid
    this.preferenceService.getPreferenceValueForPage(Constants.COLUMN_STATE_PREFERENCE_NAME, this.preferencesPageName).subscribe( (aPreference: GetOnePreferenceDTO) => {
      // REST call came back.  I have the grid preferences

      if (! aPreference.value) {
        // There are no preferences, size the columns to fit
        this.gridApi.sizeColumnsToFit()
      }
      else {

        this.userHasPastColumnState = true;

        // There is past column state
        let storedColumnStateObject = JSON.parse(aPreference.value);

        // Set the grid to use past column state
        this.gridColumnApi.applyColumnState({ state: storedColumnStateObject} );
      }

      // Get the preferences for the filters
      this.preferenceService.getPreferenceValueForPage(Constants.EXCEPTION_FILTER_PREFERENCE_NAME, this.preferencesPageName).subscribe((aPreference: GetOnePreferenceDTO) => {
        let selectedFilterToUse: number;

        if (!aPreference.value) {
          // Theres no past saved filter number
          selectedFilterToUse = this.DEFAULT_FILTER;
        }
        else {
          // There is a past saved filter number
          selectedFilterToUse = Number(aPreference.value);
          this.selectedFilter = selectedFilterToUse;
        }

        // Load the grid
        this.applyFilterAndReloadGrid(selectedFilterToUse);
      });
    });

  }


  public applyFilterAndReloadGrid(aNewFilterNumber: number) {
    if (this.selectedFilter != aNewFilterNumber) {
      // The user is changing the filter, invoke the REST call to save it
      this.preferenceService.setPreferenceValueForPage(Constants.EXCEPTION_FILTER_PREFERENCE_NAME, aNewFilterNumber, this.preferencesPageName).subscribe()
    }

    this.selectedFilter = aNewFilterNumber;

    // Show the loading message
    this.gridApi.showLoadingOverlay()

    // Invoke the REST endpoint and import the data with a passed-in filter number
    this.exceptionService.getListOfExceptions(aNewFilterNumber).subscribe((aData: GetExceptionDTO[]) => {
      // We got data from the REST call

      this.updateFilterLabel(aNewFilterNumber);

      // Put the data into the grid
      this.gridApi.setRowData(aData)

    });

  }


  private updateFilterLabel(aFilterNumber: number) {
    if (aFilterNumber == this.LAST_ONE_DAY_FILTER) {
      this.selectedFilterLabel = "Filtering records to the last 1 day"
    }
    else if (aFilterNumber == this.LAST_SEVEN_DAY_FILTER) {
      this.selectedFilterLabel = "Filtering records to the last 7 days"
    }
    else if (aFilterNumber == this.LAST_THIRTY_DAY_FILTER) {
      this.selectedFilterLabel = "Filtering records to the last 30 days"
    }
    else if (aFilterNumber == this.YEAR_TO_DATE_FILTER) {
      this.selectedFilterLabel = "Filtering records to beginning of this year"
    }
    else if (aFilterNumber == this.SHOW_ALL_DATA_FILTER) {
      this.selectedFilterLabel = ""
    }
  }


  private openSideBar(aData: GetExceptionDTO) {
    this.selectedRow = aData;
    this.isSideBarVisible = true;
  }

  public closeSideBar(): void {
    this.isSideBarVisible = false;
  }


  /*
   * User pressed the "View Details" icon in the Action Renderer
   */
  private viewDetailsClicked(aParams: ICellRendererParams): void {
    let selectedRow: GetExceptionDTO = aParams.data;

    // Open the side bar
    this.openSideBar(selectedRow);
  }


  @HostListener('document:keydown.escape', ['$event'])
  onKeydownHandler() {
    // User pressed the Escape key.  So, close the sidenav (if it's open)
    this.closeSideBar();
  }


  @HostListener('window:resize', ['$event'])
  onResize(event: Event): void {
    this.resizeSubject.next(event);  // Only run this once every 150ms - it's heavy
  }
}
