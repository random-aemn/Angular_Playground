import { Component } from '@angular/core';
import {SymbolService} from "../services/symbol.service";
import {FormBuilder, FormGroup} from "@angular/forms";
import {debounceTime, Observable, startWith, switchMap} from "rxjs";
import {AutoCompleteMatchDTO} from "../models/auto-complete-match-dto";

@Component({
  selector: 'app-auto-complete',
  templateUrl: './auto-complete.component.html',
  styleUrls: ['./auto-complete.component.scss']
})
export class AutoCompleteComponent {

  public myForm: FormGroup;
  public obsSearchMatchesToShow: Observable<AutoCompleteMatchDTO[]>

  constructor(private symbolService: SymbolService,
              private formBuilder: FormBuilder){}

  public ngOnInit(){
    this.myForm = this.formBuilder.group({
      symbol: [null, null]
    });

    // The user typed something new into the textbox - get it here
    this.myForm.controls.symbol.valueChanges.pipe(
      startWith(''),
      debounceTime(250),
      switchMap((aRawQuery: string) => {

        return this.symbolService.runSymbolSearch(aRawQuery, 5);
    })
    );


  }





}

