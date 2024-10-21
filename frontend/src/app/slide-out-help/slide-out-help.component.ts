import {Component, HostListener} from '@angular/core';

@Component({
  selector: 'app-slide-out-help',
  templateUrl: './slide-out-help.component.html',
  styleUrls: ['./slide-out-help.component.scss']
})
export class SlideOutHelpComponent {

  public helpBarIsShowing: boolean = false;

  public openHelp(){
    this.helpBarIsShowing = true;

  }

  public closeHelp(){
    this.helpBarIsShowing = false;

  }

  // Window:
  // Represents the browser window itself.
  // Provides access to global variables, functions, and objects like alert(), setTimeout(), and location.
  // The top-level object in the browser's JavaScript environment.

  // Document:
  // Represents the HTML document loaded in the window.
  // Provides access to the HTML elements within the document, allowing you to manipulate them using JavaScript.
  // Methods like getElementById(), querySelector(), and createElement() are available on the document object.

  // Body:
  // Represents the <body> element of the HTML document.
  // Contains the visible content of the page.
  // You can access the body element using document.body.
  @HostListener('document:keydown.escape', ['$event'])
  public escapeKeyHandler() {
    //Escape key is pressed so always close help
    this.helpBarIsShowing = false;
  }

}
