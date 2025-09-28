import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, switchMap } from 'rxjs';
import { ClientDetailsCpf } from '../models/client-details.model';

@Injectable({
  providedIn: 'root'
})
export class SolicitacoesService {

  private LIST_API_URL = 'http://localhost:3000/clients';
  private USER_URL = 'http://localhost:3000/users';

  constructor(private http: HttpClient) { }

  listRequests() : Observable<ClientDetailsCpf[]> {
    return this.http.get<ClientDetailsCpf[]>(`${this.LIST_API_URL}?status=PENDENT`);
  }

  //Altera o status da conta do cliente para aprovado (no caso, das solicitações, a conta é ativada pelo método activateAccount)
  approveRequest(cpf: string) : Observable<any>{
    return this.http.get<any[]>(`${this.LIST_API_URL}?cpf=${cpf}`).pipe(
      switchMap(clientes =>{
        if(clientes.length === 0){
          return of({error: 'Cliente não encontrado.'});
        }

        const cliente = clientes[0];
        const idCliente = cliente.id;
        this.activateAccount(cliente.cpf).subscribe({
          next: (response) => {
            console.log("Deu boa!");
          },
          error: (err) => {
            console.log("Erro: ", err);
          }
        });
        return this.http.patch(`${this.LIST_API_URL}/${idCliente}`, {status: 'APPROVED'})
      })
    )
  }

  //Ativa a conta do usuário
  activateAccount(cpf: string): Observable<any>{
    return this.http.get<any[]>(`${this.USER_URL}?cpf=${cpf}`).pipe(
      switchMap(users => {
        if (users.length === 0){
          return of ({error: "Usuário não encontrado."});
        }
        const userId = users[0].id;

        return this.http.patch(`${this.USER_URL}/${userId}`, {status: "ACTIVE"});
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
