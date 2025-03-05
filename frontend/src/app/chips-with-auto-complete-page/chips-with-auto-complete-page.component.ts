import { Component } from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-chips-with-auto-complete-page',
  templateUrl: './chips-with-auto-complete-page.component.html',
  styleUrls: ['./chips-with-auto-complete-page.component.scss']
})
export class ChipsWithAutoCompletePageComponent {

  constructor(private formBuilder: FormBuilder) {
  }
  public myForm: FormGroup;

  public ngOnInit(): void {
    this.myForm = this.formBuilder.group({
      startDate: [null, null],
      endDate: [null, null],
      teams: [null, null],
    })
  }

  public submit(){
    // Does nothing
  }

  public resetForm(){
    this.myForm.reset();

  }

}
