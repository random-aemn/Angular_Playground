import { Injectable } from '@angular/core';
import {Observable, of} from "rxjs";
import {AutoCompleteMatchDTO} from "../models/auto-complete-match-dto";

@Injectable({
  providedIn: 'root'
})
export class SymbolService {

  constructor() { }

  public runSymbolSearch(aRawQueryString: string, numMatchesToReturn: number): Observable<AutoCompleteMatchDTO[]>{
    let results: AutoCompleteMatchDTO[] = [];

    let upperTrimmedQueryString: string = aRawQueryString.trim().toUpperCase();

    if(upperTrimmedQueryString == null || upperTrimmedQueryString == ""){
      return of(results);
    }

    if(upperTrimmedQueryString.startsWith('V')){
      let stock1: AutoCompleteMatchDTO = new AutoCompleteMatchDTO("V", "Visa Inc.");
      let stock2: AutoCompleteMatchDTO = new AutoCompleteMatchDTO("V", "Vanguard S&P 500 E.");
      let stock3: AutoCompleteMatchDTO = new AutoCompleteMatchDTO("VOO", "Vanguard Total Stock");
      let stock4: AutoCompleteMatchDTO = new AutoCompleteMatchDTO("VTI", "Verizon Communications");
      let stock5: AutoCompleteMatchDTO = new AutoCompleteMatchDTO("VZ", "Vicinity Motor Corp.");

      results.push(stock1, stock2, stock3, stock4, stock5);

      return of(results);
    }

    if(upperTrimmedQueryString.startsWith('B')){
      let stock1: AutoCompleteMatchDTO = new AutoCompleteMatchDTO("B", "Barnes Group, Inc.");
      let stock2: AutoCompleteMatchDTO = new AutoCompleteMatchDTO("BA", "Boeing Company");
      let stock3: AutoCompleteMatchDTO = new AutoCompleteMatchDTO("BYND", "Beyond Meat, Inc.");
      let stock4: AutoCompleteMatchDTO = new AutoCompleteMatchDTO("BRKA", "Berkshire Hathaway A Stock");
      let stock5: AutoCompleteMatchDTO = new AutoCompleteMatchDTO("BRKB", "Berskshire Hathaway B Stock");

      results.push(stock1, stock2, stock3, stock4, stock5);

      return of(results);
    }





    return of(results);
  }

}
