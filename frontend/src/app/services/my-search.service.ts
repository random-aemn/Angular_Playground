import { Injectable } from '@angular/core';
import {delay, Observable, of} from "rxjs";
import {SavedSearchDTO} from "../models/saved-search-dto";

@Injectable({
  providedIn: 'root'
})
export class MySearchService {

  constructor() { }

  public getUsersSavedSearches(): Observable<SavedSearchDTO[]> {
    let savedSearches: SavedSearchDTO[] = [

      {
        id: 1,
        name: "Bugs Bunny",
        search_query: "Who's the best animated rabbit",
        last_executed_date: "12/17/2024"
      },
      {
        id: 2,
        name: "Michael Jordan",
        search_query: "Who played the basketball star in Space Jam?",
        last_executed_date: "10/20/2012"
      },
      {
        id: 3,
        name: "MonStars",
        search_query: "What was the name of the 'evil' team in Space Jam?",
        last_executed_date: "01/01/2025"
      }

    ]

    return of(savedSearches);
  }

}
