import { Injectable } from '@angular/core';
import {delay, Observable, of} from "rxjs";
import {GridWithChipSelectionDTO} from "../models/grid-with-chip-selection-dto";

@Injectable({
  providedIn: 'root'
})
export class MyContractService {

  constructor() { }

  public getContractReviewers(): Observable<GridWithChipSelectionDTO[]>{

    let data: GridWithChipSelectionDTO[] = [
      {
        user_id: 1,
        full_name: 'Luke Skywalker',
        primary_org:   'The Alliance',
        title:          'Jedi',
        secondary_org: null
      },
      {
        user_id: 2,
        full_name: 'Han Solo',
        primary_org:   'The Alliance',
        title:          'Space Pirates',
        secondary_org: null
      },
      {
        user_id: 3,
        full_name: 'Princess Leia',
        primary_org:   'The Alliance',
        title:          'Leadership',
        secondary_org: null
      },
      {
        user_id: 4,
        full_name: 'Chewbaccaa',
        primary_org:   'The Alliance',
        title:          'Space Pirates',
        secondary_org: null
      },
      {
        user_id: 5,
        full_name: 'Obi-Wan Kinobi',
        primary_org:   'The Alliance',
        title:          'Jedi',
        secondary_org: null
      },
      {
        user_id: 6,
        full_name: 'Cassian Andor',
        primary_org:   'The Alliance',
        title:          'Rebels',
        secondary_org: null
      },
      {
        user_id: 7,
        full_name: 'Saw Guererra',
        primary_org:   'Independent',
        title:          'Gun for Hire',
        secondary_org: null
      },
      {
        user_id: 8,
        full_name: 'K-2SO',
        primary_org:   'The Alliance',
        title:          'Sarcastic Droids',
        secondary_org: null
      },
      {
        user_id: 9,
        full_name: 'Galen Erso',
        primary_org:   'The Empire',
        title:          'Planet Destroying Scientists',
        secondary_org: null
      },
      {
        user_id: 10,
        full_name: 'Jyn Erso',
        primary_org:   'The Alliance',
        title:          'Rebels abandoned by their planet-destroying parents',
        secondary_org: null
      },
      {
        user_id: 11,
        full_name: 'Chirrut Imwe',
        primary_org:   'Independent',
        title:          'I am one with the force.  And, the force is one with me.',
        secondary_org: null
      },
      {
        user_id: 12,
        full_name: 'Baze Malbus',
        primary_org:   'Independent',
        title:          'Gun for Hire',
        secondary_org: null
      },
      {
        user_id: 13,
        full_name: 'Darth Vader',
        primary_org:   'The Empire',
        title:          'Ultimate Bad Ass Sith',
        secondary_org: null
      },
      {
        user_id: 14,
        full_name: 'Grand Moff Tarkin',
        primary_org:   'The Empire',
        title:          'Management with a stick up his ass',
        secondary_org: null
      },
      {
        user_id: 15,
        full_name: 'Din Djarin',
        primary_org:   'Mandalorians',
        title:          'In a category of his own',
        secondary_org: null
      }
    ]

    return of(data);

  }

  /*
  this method has a return type of Observable because \
  it will return a message, but that message MIGHT be nothing, therefore the <void>
   */
  public addContractReviewer(aUserId: number): Observable<null>{
    // Simulating a REST call that takes 3 seconds to return data or nothing
    return of(null).pipe(delay(1));
  }

  public removeContractReviewer(aUserId: number): Observable<null> {

    return of(null).pipe(delay(1));

  }

}
