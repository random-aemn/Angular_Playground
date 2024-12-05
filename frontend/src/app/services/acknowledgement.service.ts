import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})


export class AcknowledgementService {

  constructor(private httpClient: HttpClient) { }


  public markUserAsAcknowledged(): Observable<String>{

  const restURL: string = environment.baseUrl + "/api/set/acknowledge";

  return this.httpClient.put(restURL, {}, {responseType: 'text'});

  }
}
