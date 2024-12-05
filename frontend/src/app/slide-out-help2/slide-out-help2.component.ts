import {Component, HostListener} from '@angular/core';

@Component({
  selector: 'app-slide-out-help2',
  templateUrl: './slide-out-help2.component.html',
  styleUrls: ['./slide-out-help2.component.scss']
})
export class SlideOutHelp2Component {
  // a boolean for the help bar showing, initialized to false
  public helpShowing: boolean = false;

  public openHelp(){
    this.helpShowing = true;
  }

  public closeHelp(){
    this.helpShowing = false;
  }

  @HostListener('document:keydown.escape', ['$event'])
  public escapeKeyHandler() {
    //Escape key is pressed so always close help
    // this.helpShowing = false;
    this.closeHelp()
  }


}
