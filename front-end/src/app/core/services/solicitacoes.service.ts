import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, switchMap } from 'rxjs';
import { ClientDetailsCpf } from '../models/client-details.model';

@Injectable({
  providedIn: 'root'
})
export class SolicitacoesService {

  private CLIENTES_API_URL = 'http://localhost:3000/clientes';

  constructor(private http: HttpClient) { }

  listRequests() : Observable<ClientDetailsCpf[]> {
    return this.http.get<ClientDetailsCpf[]>(`${this.CLIENTES_API_URL}?filtro=para_aprovar`);
  }

  approveRequest(cpf: string) : Observable<any>{
    const url = `${this.CLIENTES_API_URL}/${cpf}/aprovar`;
    return this.http.post(url, {});
  }

  //Ativa a conta do usuário
  activateAccount(cpf: string): Observable<any>{
    return this.http.get<any[]>(`${this.CLIENTES_API_URL}?cpf=${cpf}`).pipe(
      switchMap(users => {
        if (users.length === 0){
          return of ({error: "Usuário não encontrado."});
        }
        const userId = users[0].id;

        return this.http.patch(`${this.CLIENTES_API_URL}/${userId}`, {status: "ACTIVE"});
      })
    )
  }

  denyRequest(cpf: string) : Observable<any>{
    const url = `${this.CLIENTES_API_URL}/${cpf}/rejeitar`;
    const motivoRejeicao = { motivo: "Não atende aos critérios do banco." };
    return this.http.post(url, motivoRejeicao);
  }
}
