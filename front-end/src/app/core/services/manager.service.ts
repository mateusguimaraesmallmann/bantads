import { Injectable, signal, computed } from '@angular/core';
import { GerenteComClientes, ManagerDto } from '../models/manager';
import { Manager } from '../models/manager';
import { User } from '../models/user.model';
import { ClientDetails } from '../models/client-details.model';
import { HttpClient } from '@angular/common/http';
import { forkJoin, map, Observable, of, switchMap, throwError } from 'rxjs';

const MANAGER_URL = 'http://localhost:3000/gerentes';
const CLIENTES_URL = 'http://localhost:3000/clientes';

@Injectable({ providedIn: 'root' })
export class ManagerService {

  constructor(private http: HttpClient){

  }

  private readonly _gerentes = signal<GerenteComClientes[]>([
    {
      id: 1, nome: 'Ana Souza', cpf: '111.222.333-44', email: 'ana@bantads.com', telefone: '(41) 90000-0001',
      clientes: [
        { id: 101, nome: 'Carlos', saldo: 1500.00 },
        { id: 102, nome: 'Beatriz', saldo: 0.00 },
        { id: 103, nome: 'Rafaela', saldo: -200.50 },
      ]
    },
    {
      id: 2, nome: 'Bruno Lima', cpf: '555.666.777-88', email: 'bruno@bantads.com', telefone: '(41) 90000-0002',
      clientes: [
        { id: 104, nome: 'Diego', saldo: 3200.00 },
        { id: 105, nome: 'Marina', saldo: 50.00 },
        { id: 106, nome: 'Otávio', saldo: -1000.00 },
      ]
    },
    {
      id: 3, nome: 'Carla Mendes', cpf: '999.000.111-22', email: 'carla@bantads.com', telefone: '(41) 90000-0003',
      clientes: [
        { id: 107, nome: 'Sofia', saldo: -40.00 },
        { id: 108, nome: 'João', saldo: -10.00 },
      ]
    }
  ]);

  readonly gerentes = computed(() => this._gerentes());

  //#region Cria Gerente
  //addManager(manager: ManagerDto): Observable<Manager>{
  //    return this.http.get<User[]>(`${USER_URL}?cpf=${manager.cpf}`).pipe(
  //      switchMap(existingManagers => {
  //          if (existingManagers.length > 0){
  //            return throwError(() => new Error ("CPF já cadastrado no sistema."));
  //          }
  //
  //          //Cria o DTO para carregar os dados de usuário
  //          const newUser : User = {
  //            name: manager.nome,
  //            email: manager.email,
  //            cpf: manager.cpf,
  //            password: manager.senha,
  //            role: "MANAGER",
  //            status: "ACTIVE"
  //          }
  //
  //          return this.http.post<User> (USER_URL, newUser);
  //      }),
  //      switchMap(createdUser => {
  //
  //        //Cria o DTO para carregar os dados do gerente
  //        const newManagerPayload = {
  //          name: manager.nome,
  //          email: manager.email,
  //          cpf: manager.cpf,
  //          phone: manager.telefone
  //        };
  //        return this.http.post<Manager>(MANAGER_URL, newManagerPayload);
  //      })
  //    )
  //}

  addManager(manager: ManagerDto): Observable<Manager>{

      const newManagerPayload = {
        nome: manager.nome,
        email: manager.email,
        cpf: manager.cpf,
        senha: manager.senha,
      };

      return this.http.post<Manager>(MANAGER_URL, newManagerPayload);
  }

  //#region Lista os Gerentes
  //listManagers(): Observable<Manager[]> {
  //  return this.http.get<Manager[]>(`${USER_URL}?role=MANAGER&&status=ACTIVE`).pipe(
  //    switchMap(activeUsers =>{
  //      if (!activeUsers || activeUsers.length === 0){
  //        return of([]);
  //      }
  //      const activeManagersCpf = activeUsers.map(user => user.cpf)
  //
  //      return this.http.get<any[]>(MANAGER_URL).pipe(
  //        map(allManagers =>{
  //          return allManagers.filter(manager => activeManagersCpf.includes(manager.cpf))
  //          .map(apiGerente => ({
  //              id: apiGerente.id,
  //              nome: apiGerente.name,
  //              email: apiGerente.email,
  //              cpf: apiGerente.cpf,
  //              telefone: apiGerente.phone
  //            }))
  //          })
  //      )
  //    })
  //  );
  //}

  listManagers(): Observable<Manager[]> {
    return this.http.get<Manager[]>(MANAGER_URL).pipe(
      map(allManagers => allManagers.map(apiGerente => ({
        id: apiGerente.id,
        nome: apiGerente.nome,
        email: apiGerente.email,
        cpf: apiGerente.cpf,
        telefone: apiGerente.telefone
      } as Manager)))
    );
  }

  //#region Desabilita um Gerente
  //disableManager(cpf: string): Observable<any>{
  //  return this.http.get<any[]>(`${USER_URL}?cpf=${cpf}`).pipe(
  //    switchMap(managers =>{
  //      if (managers.length === 0){
  //        return of({error: 'Gerente não encontrado.'});
  //      }
  //
  //      const manager = managers[0];
  //      const idManager = manager.id;
  //
  //      return this.http.patch(`${USER_URL}/${idManager}`, {status: 'DISABLED'});
  //      console.log("Usuário desligado com sucesso!");
  //    })
  //  )
  //}

  disableManager(cpf: string): Observable<any>{
    const url = `${MANAGER_URL}/${cpf}`;
    return this.http.delete(url);
  }

  //#region Atualiza o Gerente
  //updateManager(manager: ManagerDto): Observable<any>{
  //  const findUser = this.http.get<any[]>(`${USER_URL}?cpf=${manager.cpf}`);
  //  const findManager = this.http.get<any[]>(`${MANAGER_URL}?cpf=${manager.cpf}`);

  //  return forkJoin([findUser, findManager]).pipe(
  //    switchMap(([users, managers]) =>{
  //      if (!users || users.length === 0){
  //        return of ({error: "Usuário com CPF especificado não foi encontrado."});
  //      }
  //      if (!managers || managers.length === 0){
  //        return of ({error: "Gerente com CPF especificado não foi encontrado."});
  //      }
  //      const userToUpdate = users[0];
  //      const managerToUpdate = managers[0];
  //      console.log(userToUpdate, managerToUpdate)
  //
  //      const userPayload = {
  //        name: manager.nome,
  //        cpf: manager.cpf,
  //        email: manager.email,
  //        password: manager.senha
  //      }
  //
  //      const managerPayload = {
  //        name: manager.nome,
  //        cpf: manager.cpf,
  //        email: manager.email,
  //        phone: manager.telefone,
  //      }
  //
  //      console.log(userPayload, managerPayload)
  //
  //      const updateUser = this.http.patch(`${USER_URL}/${userToUpdate.id}`, userPayload);
  //      const updateManager = this.http.patch(`${MANAGER_URL}/${managerToUpdate.id}`, managerPayload)
  //
  //      return forkJoin([updateUser, updateManager]);
  //    }
  //  )
  //)
  //}

  updateManager(manager: ManagerDto): Observable<any>{
    const url = `${MANAGER_URL}/${manager.cpf}`;

    const managerPayload = {
      nome: manager.nome,
      email: manager.email,
      senha: manager.senha,
    }

    return this.http.put(url, managerPayload);
  }

  listarTodosOsClientesManager(): Observable<ClientDetails[]>{
      return this.http.get<ClientDetails[]>(CLIENTES_URL);
  }

}
