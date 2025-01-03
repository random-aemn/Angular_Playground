import { Injectable } from '@angular/core';
import {EditContractSpecDTO} from "../models/EditContractSpecDTO";
import {delay, Observable, of} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ContractSpecService {

  constructor() { }

  public getEditContractSpecInfo(): Observable<EditContractSpecDTO> {

    let tempDto: EditContractSpecDTO = new EditContractSpecDTO();

    tempDto.contractSpecName = "Bugs Bunny";
    tempDto.priorityId = 3;
    tempDto.executionDate = "12/15/2024";
    tempDto.extraDays = 5;

    // of is how one converts a DTO into an observable
    return of(tempDto);

    // Example of how to simulate a slow REST call
    // return of(tempDto).pipe(delay(4000));

  }
}
