import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class FormUtilsService {

  constructor() { }

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
