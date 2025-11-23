import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs'; // Importa Observable
import { ClientUpdate } from '../models/client-update.model';
import {ClientDetails} from '../models/client-details.model';
import { ClienteCompleto } from '../models/account.model';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  private apiUrl = 'http://localhost:3000/clientes';

  constructor(private http: HttpClient) { }

  consultarUsuario(cpf:string):Observable<ClientUpdate>{
    const url = `${this.apiUrl}/${cpf}`;
    return this.http.get<ClientUpdate>(url);
  }

  atualizarUsuario(user: ClientUpdate, cpf:string): Observable<ClientUpdate> {
    const url = `${this.apiUrl}/${cpf}`;
    return this.http.put<ClientUpdate>(url, user);
  }

  listarDetalhesCliente(cpf:string) : Observable<ClientDetails>{
    const url = this.apiUrl + "/" + cpf;
    return this.http.get<ClientDetails>(url);
  }

  returnAllClients(): Observable<ClientDetails[]>{
    return this.http.get<ClientDetails[]>(this.apiUrl);
  }

  consultarClienteSaga(cpf:string ):Observable<ClienteCompleto>{
    const url = this.apiUrl + "/" + cpf;
    return this.http.get<ClienteCompleto>(url);
  }
  
}
