export interface Manager {
  id: number;
  nome: string;
  cpf: string;
  email: string;
  telefone: string;
}

export interface ManagerDto{
  nome: string,
  cpf: string,
  email: string,
  telefone: string,
  senha: string
}

export interface Cliente {
  id: number;
  nome: string;
  saldo: number;
}

export interface GerenteComClientes extends Manager {
  clientes: Cliente[];
}
