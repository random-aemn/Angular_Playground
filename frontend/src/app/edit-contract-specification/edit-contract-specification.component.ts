import { Component } from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {ContractSpecService} from "../services/contract-spec.service";
import {Observable, tap} from "rxjs";
import {EditContractSpecDTO} from "../models/EditContractSpecDTO";

@Component({
  selector: 'app-edit-contract-specification',
  templateUrl: './edit-contract-specification.component.html',
  styleUrls: ['./edit-contract-specification.component.scss']
})
export class EditContractSpecificationComponent {

  public myForm: FormGroup;
  public contractSpecObs: Observable<EditContractSpecDTO>;

  constructor(private formBuilder: FormBuilder,
              private contractSpecService: ContractSpecService) {
  }

  // Use the ngOnInit method to initialize the myForm object
  public ngOnInit(): void{
    this.myForm = this.formBuilder.group({
      contractSpecName: [null, null],
      priority: [null, null],
      contractSpecExecutionDate: [null, null],
      extraDays: [null, null],
    })

    // pipe is adding operations to the observable; tap is one of pipe's operations that gives you access to the REST call's data
    this.contractSpecObs = this.contractSpecService.getEditContractSpecInfo().pipe(
      tap((aData: EditContractSpecDTO)=> {
        // The REST call came back and so we are loading the form
        this.myForm.controls.contractSpecName.setValue(aData.contractSpecName);
        this.myForm.controls.priority.setValue(aData.priorityId);
        // convert the string to a date and then pass the date
        let date: Date = new Date(aData.executionDate);
        this.myForm.controls.contractSpecExecutionDate.setValue(date);
        this.myForm.controls.extraDays.setValue(aData.extraDays);


        }
      )

    )

  }



}
