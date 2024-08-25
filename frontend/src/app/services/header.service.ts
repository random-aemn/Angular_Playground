import { Injectable } from '@angular/core';
import {GetOnePreferenceDTO} from "../models/preferences/get-one-preference-dto";
import {BehaviorSubject, Observable} from "rxjs";
import {PreferenceService} from "./preference.service";
import {HeaderUrlDTO} from "../models/header-url-dto";
import {Constants} from "../utilities/constants";

@Injectable({
  providedIn: 'root'
})
export class HeaderService {

  public headerThemesSubject: BehaviorSubject<HeaderUrlDTO>;

  public listOfHeaderDTO: HeaderUrlDTO[] = [
    {
      url: './assets/images/wavyDotsHeader.png',
      isRightAligned: false,
      pixelWidth: 3500,
    },
    {
      url: './assets/images/diamondDotsHeader.png',
      isRightAligned: true,
      pixelWidth: 2587,
    },

    {
      url: './assets/images/gradientEarthHeader.png',
      isRightAligned: true,
      pixelWidth: 1406,
    },
    {
      url: './assets/images/fractalEarthHeader.png',
      isRightAligned: true,
      pixelWidth: 1245,
    },
    {
      url: './assets/images/wavyEarthHeader.png',
      isRightAligned: true,
      pixelWidth: 726,
    },
  ]

  constructor(private preferenceService: PreferenceService) { }

  public initializeService(aValue: GetOnePreferenceDTO): void {
    // Initialize the state after we've queried the preferences table
    let initialDTO: HeaderUrlDTO = new HeaderUrlDTO();
    if (aValue.value == null) {
      initialDTO.url = './assets/images/wavyDotsHeader.png';
      initialDTO.isRightAligned = false;
    }
    if (aValue.value != null) {
      initialDTO = JSON.parse(aValue.value);
    }


    // Anyone who is listening will get this initial value
    this.headerThemesSubject = new BehaviorSubject(initialDTO);
  }

  public getHeaderThemeDTOs(): HeaderUrlDTO[] {
    return this.listOfHeaderDTO;
  }

  public getHeaderObservable(): Observable<HeaderUrlDTO> {
    return this.headerThemesSubject.asObservable();
  }

  public updateHeaderThemeDTO(aNewDTO: HeaderUrlDTO): void {

    // Update the state in the preferences table so things are persistent
    this.preferenceService.setPreferenceValueWithoutPageUsingJson(Constants.HEADER_THEME_PREFERENCE_NAME, aNewDTO).subscribe()

    // Send the message that indicates to the rest of the app that the navbar has updated
    this.headerThemesSubject.next(aNewDTO);
  }

}
