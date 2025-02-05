import { Component } from '@angular/core';

@Component({
  selector: 'app-four-tab-page',
  templateUrl: './four-tab-page.component.html',
  styleUrls: ['./four-tab-page.component.scss']
})
export class FourTabPageComponent {

  public selectedIndex: number = 0;


  ngOnInit(): void{

  }
}
