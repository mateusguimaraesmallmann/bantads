import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NewUser } from '../../models/new-client.model';
import { Observable } from 'rxjs';

const API_GATEWAY_URL = 'http://localhost:3000/autocadastro';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private http: HttpClient) { }

  registerClient (client: NewUser) : Observable<any>{
    return this.http.post<NewUser>(API_GATEWAY_URL, client);
  }
}
