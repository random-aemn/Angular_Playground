import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DateService {

  constructor() { }

  /*
   * This is the method that the ag-grid calls to sort date fields
   */
  public dateComparator(aDate1: string, aDate2: string): number {
    // Convert the date string into a number
    let date1Number = this.dateToNumber(aDate1);
    let date2Number = this.dateToNumber(aDate2);

    if (date1Number === null && date2Number === null) {
      return 0;
    }
    if (date1Number === null) {
      return -1;
    }
    if (date2Number === null) {
      return 1;
    }

    return date1Number - date2Number;
  }


  /*
   * Convert this date format into a number
   *     mm/dd/yyyy HH24:MI:SS --> yyyymmdd
   *
   * For example: 01/26/2023 00:02:52 --> 20230126
   */
  private dateToNumber(aDateAsString: string): number | null {
    if (aDateAsString === undefined || aDateAsString === null) {
      return null;
    }

    if (aDateAsString.length == 10) {
      // Assume that 10 character date is in the format of mm/dd/yyyy

      // Pull the parts of the date out of the 10-character string
      let yearNumber: string  = aDateAsString.substring(6, 10);
      let monthNumber: string = aDateAsString.substring(0, 2);
      let dayNumber: string   = aDateAsString.substring(3, 5);

      // Generate a number from the parts of the date
      let resultAsString =  `${yearNumber}${monthNumber}${dayNumber}`;
      return Number(resultAsString);
    }
    else if (aDateAsString.length == 19) {
      // Assume that 19 character date is in the format of mm/dd/yyyy hh24:mi:ss

      // Pull the parts of the date out of the 19-character string
      let yearNumber:   string = aDateAsString.substring(6, 10);
      let monthNumber:  string = aDateAsString.substring(0, 2);
      let dayNumber:    string = aDateAsString.substring(3, 5);
      let hourNumber:   string = aDateAsString.substring(11, 13);
      let minuteNumber: string = aDateAsString.substring(14, 16);
      let secondNumber: string = aDateAsString.substring(17, 19);

      // Generate a number from the parts of the date
      let resultAsString =  `${yearNumber}${monthNumber}${dayNumber}${hourNumber}${minuteNumber}${secondNumber}`;
      return Number(resultAsString);
    }
    else {
      // The date is neither 10 nor 19 characters long.  It is invalid
      return null;
    }

  }


}
