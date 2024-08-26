import { Component } from '@angular/core';
import {ICellRendererAngularComp} from "ag-grid-angular";
import {ICellRendererParams} from "ag-grid-community";

@Component({
  selector: 'app-list-exceptions-action-renderer',
  templateUrl: './list-exceptions-action-renderer.component.html',
  styleUrls: ['./list-exceptions-action-renderer.component.scss']
})
export class ListExceptionsActionRendererComponent implements ICellRendererAngularComp {

  private params: ICellRendererParams;

  public agInit(aParams: ICellRendererParams): void {
    this.params = aParams;
  }

  public refresh(aParams: ICellRendererParams<any>): boolean {
    return false;
  }

  public viewDetailsClicked(): void {
    //@ts-ignore
    this.params.viewDetailsClicked(this.params);
  }
}
