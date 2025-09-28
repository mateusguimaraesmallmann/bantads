import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NewUser } from '../../models/new-client.model';
import { Observable, switchMap, of, throwError } from 'rxjs';

const BASE_URL = 'http://localhost:3000/clients';
const USER_URL = 'http://localhost:3000/users';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private http: HttpClient) { }

  registerClient (client: NewUser) : Observable<any>{
    return this.http.get<NewUser[]>(`${BASE_URL}?cpf=${client.cpf}`).pipe(
      switchMap(existingClients =>{
        if(existingClients.length > 0){
          return throwError(() => new Error('CPF já cadstrado no sistema.'));
        }
        return this.http.post<NewUser>(BASE_URL, client)
      }),
      switchMap(createUser => {
        const newUserPayload = {
          name: client.nome,
          email: client.email,
          cpf: client.cpf,
          password: "tads",
          role: client.role,
          status: "DISABLED"
        }

        return this.http.post(`${USER_URL}`, newUserPayload);
      })
    )
  }
}
