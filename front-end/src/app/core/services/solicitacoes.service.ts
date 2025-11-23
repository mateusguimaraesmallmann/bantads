import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, Observable, of, switchMap, map } from 'rxjs';
import { ClientDetailsCpf } from '../models/client-details.model';

@Injectable({
  providedIn: 'root'
})
export class SolicitacoesService {

  private BASE_URL = "http://localhost:3000";
  private CLIENTES_API_URL = 'http://localhost:3000/clientes';

  constructor(private http: HttpClient) { }

  listRequestsByManager(managerId: number): Observable<ClientDetailsCpf[]> | null {
    return this.http.get<any[]>(`${this.BASE_URL}/contas/gerente/${managerId}/pendentes`).pipe(
      switchMap(contas => {
        console.log(contas)
        if (!contas || contas.length === 0) return of([]);

        const requests = contas.map(conta =>
          this.http.get<any>(`${this.BASE_URL}/clientes/id/${conta.idCliente}`).pipe(
            map(cliente => ({
              cpf: cliente.cpf,
              nome: cliente.nome,
              telefone: cliente.telefone,
              email: cliente.email,
              endereco: cliente.endereco ? `${cliente.endereco.logradouro}, ${cliente.endereco.numero}` : '',
              cidade: cliente.endereco ? cliente.endereco.cidade : '',
              estado: cliente.endereco ? cliente.endereco.estado : '',
              salario: cliente.salario,
              conta: conta.numero ? conta.numero : 'Pendente',
              saldo: conta.saldo,
              limite: conta.limite,
              gerente: managerId.toString(),
              gerente_nome: '',
              gerente_email: ''
            } as ClientDetailsCpf))
          )
        );

        return forkJoin(requests);
      })
    );
  }

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
