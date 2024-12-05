import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';

@Component({
  selector: 'app-smooth-vertical-transition1',
  templateUrl: './smooth-vertical-transition1.component.html',
  styleUrls: ['./smooth-vertical-transition1.component.scss']
})
export class SmoothVerticalTransition1Component implements AfterViewInit{

  @ViewChild("collapsableDiv")
  private collapsableDiv: ElementRef;

  private originalDivHeightInPixels: string;
  private isDivHidden: boolean = false;

  ngAfterViewInit(): void {
    this.originalDivHeightInPixels = String(this.collapsableDiv.nativeElement.offsetHeight) + "px"

    this.collapsableDiv.nativeElement.style.height = this.originalDivHeightInPixels;

  }

  // Set height of the div to the original height
  showDetails(){

  }

//   set height of the div to 0px
  hideDetails(){

  }



}
