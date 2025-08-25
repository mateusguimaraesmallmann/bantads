import { Injectable, signal, computed } from '@angular/core';
import { GerenteComClientes } from '../models/manager';

@Injectable({ providedIn: 'root' })
export class ManagerService {

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
  
}
