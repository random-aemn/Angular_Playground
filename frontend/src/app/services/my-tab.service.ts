import { Injectable } from '@angular/core';
import {Observable, of, Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MyTabService {

  constructor() { }

  private mySubject: Subject<void> = new Subject<void>();

  public sendMessgeToShowSection5InTab4():void {

    this.mySubject.next();

  }

  public listenForMessageToShowSection5InTab4(): Observable<void>{

   // return of(this.mySubject);
  return this.mySubject.asObservable()
  }


  /*
         * Scroll the page into view so the user can see the tag that has id=" "
         */
  public scrollToTargetId(aElementId: string): void {
    // Get a reference to the DOM element
    const el: HTMLElement|null = document.getElementById(aElementId);

    if (el) {
      // The DOM element exists.  So, scroll to it.
      setTimeout(() =>
        el.scrollIntoView({behavior: 'smooth', block: 'start', inline: 'nearest'}), 0);
    }
  }





}
