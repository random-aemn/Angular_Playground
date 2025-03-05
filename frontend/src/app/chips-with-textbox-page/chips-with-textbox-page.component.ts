import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {MatChipInput, MatChipInputEvent} from "@angular/material/chips";

@Component({
  selector: 'app-chips-with-textbox-page',
  templateUrl: './chips-with-textbox-page.component.html',
  styleUrls: ['./chips-with-textbox-page.component.scss']
})
export class ChipsWithTextboxPageComponent {

  constructor(private formBuilder: FormBuilder) {
  }

  public myForm: FormGroup
  public displayedAliases: string[] = [];
  public separatorKeyCodes: number[] = [ENTER, COMMA]

  public ngOnInit(): void {
    this.myForm = this.formBuilder.group({
      countryCode:  [null, Validators.required],
      countryName:  [null, Validators.required],
      alias:        [null, Validators.required],
      isCosc:       [null, null]


    })
  }

  public submitForm(){
    // Touch all the form fields so that the user sees the error messages
    this.myForm.markAllAsTouched();
    if(!this.myForm.invalid){
      //submit the form
    }
    else {
      // if it's invalid - normally just direct the user to another page
      // reject the form and tell the user something useful
    }
  }

  public resetForm(){
    this.myForm.reset();
    this.displayedAliases = [];
  }

  public addAlias(event: MatChipInputEvent){
    let inputBox: MatChipInput = event.chipInput;
    let enteredValue: string = event.value.trim();

    if (enteredValue) {
      this.displayedAliases.push(enteredValue);
      // alias is defined at runtime - so no autocomplete
      this.myForm.controls.alias.setValue(this.displayedAliases);
    }

    // Clear the text so the user can enter additional aliases
    if (inputBox) {
      inputBox.inputElement.value = "";
    }

    this.myForm.controls.alias.markAsDirty();

  }

  public removeAlias(arrayIndexToRemove: number){
    if (arrayIndexToRemove < 0 ){
      return;
    }

    // Remove the specified item from the array
    this.displayedAliases.splice(arrayIndexToRemove, 1)
    this.myForm.controls.alias.setValue(this.displayedAliases);

  }

}
