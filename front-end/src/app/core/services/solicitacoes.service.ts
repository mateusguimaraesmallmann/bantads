import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, switchMap } from 'rxjs';
import { ClientDetailsCpf } from '../models/client-details.model';

@Injectable({
  providedIn: 'root'
})
export class SolicitacoesService {

  private LIST_API_URL = 'http://localhost:3000/clients';

  constructor(private http: HttpClient) { }

  listRequests() : Observable<ClientDetailsCpf[]> {
    return this.http.get<ClientDetailsCpf[]>(`${this.LIST_API_URL}?status=PENDENT`);
  }

  approveRequest(cpf: string) : Observable<any>{
    return this.http.get<any[]>(`${this.LIST_API_URL}?cpf=${cpf}`).pipe(
      switchMap(clientes =>{
        if(clientes.length === 0){
          return of({error: 'Cliente não encontrado.'});
        }

        const cliente = clientes[0];
        const idCliente = cliente.id;
        return this.http.patch(`${this.LIST_API_URL}/${idCliente}`, {status: 'APPROVED'});
      })
    )
  }

  denyRequest(cpf: string) : Observable<any>{
    return this.http.get<any[]>(`${this.LIST_API_URL}?cpf=${cpf}`).pipe(
      switchMap(clientes =>{
        if(clientes.length === 0){
          return of({error: 'Cliente não encontrado.'});
        }

        const cliente = clientes[0];
        const idCliente = cliente.id;

        return this.http.patch(`${this.LIST_API_URL}/${idCliente}`, {status: 'DENIED'});
      })
    )
  }
}
