export interface ClientDetails {
  cpf: string;
  nome: string;
  email: string;
  telefone: string;
  endereco: string;
  cidade: string;
  estado: string;
  conta: string;
  saldo: number;
  limite: number;
}

export interface ClientDetailsCpf {
  cpf: string;
  nome: string;
  telefone: string;
  email: string;
  endereco: string;
  cidade: string;
  estado: string;
  salario: number;
  conta: string;
  saldo: number; 
  limite: number;
  gerente: string; 
  gerente_nome: string;
  gerente_email: string;
}