import { Injectable, signal, computed } from '@angular/core';
import { GerenteComClientes, ManagerDto } from '../models/manager';
import { Manager } from '../models/manager';
import { User } from '../models/user.model';
import { HttpClient } from '@angular/common/http';
import { map, Observable, switchMap, throwError } from 'rxjs';

const MANAGER_URL = 'http://localhost:3000/managers';
const USER_URL = 'http://localhost:3000/users'

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
  addManager(manager: ManagerDto): Observable<Manager>{
      return this.http.get<User[]>(`${USER_URL}?cpf=${manager.cpf}`).pipe(
        switchMap(existingManagers => {
            if (existingManagers.length > 0){
              return throwError(() => new Error ("CPF já cadastrado no sistema."));
            }

            //Cria o DTO para carregar os dados de usuário
            const newUser = {
              name: manager.nome,
              email: manager.email,
              cpf: manager.cpf,
              password: manager.senha,
              role: "MANAGER"
            }

            return this.http.post<User> (USER_URL, newUser);
        }),
        switchMap(createdUser => {

          //Cria o DTO para carregar os dados do gerente
          const newManagerPayload = {
            name: manager.nome,
            email: manager.email,
            cpf: manager.cpf,
            phone: manager.telefone
          };
          return this.http.post<Manager>(MANAGER_URL, newManagerPayload);
        })
      )
  }

  //#region Lista os Gerentes
  listManagers(): Observable<Manager[]> {
    return this.http.get<any[]>(MANAGER_URL).pipe(
      map(apiPayload =>
        apiPayload.map(apiGerente => ({
          id: apiGerente.id,
          nome: apiGerente.name,
          email: apiGerente.email,
          cpf: apiGerente.cpf,
          telefone: apiGerente.phone
        }))
      )
    );
  }

}
