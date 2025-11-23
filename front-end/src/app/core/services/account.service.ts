import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NewUser } from '../models/new-client.model';
import { forkJoin, map, Observable, of, switchMap } from 'rxjs';
import { Account } from '../models/account.model';
import { Manager } from '../models/manager';

const MANAGER_URL = 'http://localhost:3000/gerentes';
const ACCOUNTS_URL = 'http://localhost:3000/contas';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  constructor(private http: HttpClient) { }

  // public generateAccount(user: NewUser): Observable<Account> {
  //   return this.assignManager().pipe(
  //     switchMap(managerInfo => {
  //       return this.generateUniqueAccountNumber().pipe(
  //         switchMap(accountNumber => {
  //           const newAccount: Account = {
  //             number: accountNumber,
  //             clientCpf: user.cpf!,
  //             clientName: user.nome!,
  //             balance: 0.0,
  //             limit: this.calculateLimit(user.salario || 0),
  //             managerCpf: managerInfo.managerCpf,
  //             managerName: managerInfo.managerName,
  //             createdAt: new Date()
  //           };
  //           return this.http.post<Account>(ACCOUNTS_URL, newAccount);
  //         })
  //       );
  //     })
  //   );
  // }

  //#region Contas
  public returnAccountData(cpfClient: string): Observable<Account | null> {
      return this.http.get<Account[]>(`${ACCOUNTS_URL}?clientCpf=${cpfClient}`).pipe(
          map(accounts => {
              if (accounts && accounts.length > 0) {
                  return accounts[0];
              }
              return null;
          })
      );
  }

  public returnAllAccounts (): Observable<Account[]>{
    return this.http.get<Account[]>(ACCOUNTS_URL);
  }
  //#endregion

  //#region Gera o Número da Conta
  private generateUniqueAccountNumber(): Observable<number> {
    //Como pode ser gerado o número 0, soma com 1000 para garantir que o número da conta tenha 4 dígitos
    const newAccountNumber = Math.floor(Math.random() * 9000) + 1000;

    return this.http.get<Account[]>(`${ACCOUNTS_URL}?number=${newAccountNumber}`).pipe(
      switchMap(accounts => {
        if (accounts.length > 0) {
          return this.generateUniqueAccountNumber();
        } else {
          return of(newAccountNumber);
        }
      })
    );
  }

  //#region Calcula o Limite
  private calculateLimit(salary: number): number {
    if (salary < 2000) {
      return 0.0;
    }
    return salary / 2;
  }

  //#region Define o Gerente
  private assignManager(): Observable<{ managerName: string, managerCpf: string }> {
    return forkJoin({
      managers: this.http.get<Manager[]>(MANAGER_URL),
      accounts: this.http.get<Account[]>(ACCOUNTS_URL)
    }).pipe(
      map(({ managers, accounts }) => {
        if (!managers || managers.length === 0) {
          throw new Error('Nenhum gerente disponível para atribuir.');
        }

        const accountCount = new Map<string, number>();
        managers.forEach(manager => accountCount.set(manager.cpf, 0));

        accounts.forEach(account => {
          if (account.managerCpf && accountCount.has(account.managerCpf)) {
            let currentCount = accountCount.get(account.managerCpf)!;
            accountCount.set(account.managerCpf, currentCount + 1);
          }
        });

        const minManager = Math.min(...Array.from(accountCount.values()));
        let candidates = managers.filter(manager => accountCount.get(manager.cpf) === minManager);

        if (candidates.length > 1) {
          candidates.sort((a, b) => a.nome.localeCompare(b.nome));
        }

        const responseManager = candidates[0];

        return {
          managerName: responseManager.nome,
          managerCpf: responseManager.cpf
        };
      })
    );
  }
}
