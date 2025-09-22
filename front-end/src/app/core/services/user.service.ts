import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs'; // Importa Observable
import { UserUpdate } from '../models/user-update.model';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  private apiUrl = '';

  constructor(private http: HttpClient) { }

  consultarUsuario(cpf:string):Observable<UserUpdate>{
    const url = `${this.apiUrl}/${cpf}`;
    return this.http.get<UserUpdate>(url);
  }

  atualizarUsuario(user: UserUpdate, cpf:string): Observable<UserUpdate> {
    const url = `${this.apiUrl}/${cpf}`;
    let resposta = this.http.put<UserUpdate>(url, user);
    console.log(resposta)
    return of(user);
  }
}
