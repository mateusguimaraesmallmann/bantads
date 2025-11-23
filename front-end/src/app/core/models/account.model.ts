export interface Account {
  number: number;
  clientCpf: string | null;
  clientName: string | null;
  balance: number;
  limit: number;
  managerCpf: string | null;
  managerName: string | null;
  createdAt: Date;
}


//Interface para carregar os clientes com mais saldo na conta
export interface topClientAccount{
  cpf: string | null,
  clientName: string | null,
  city: string | null,
  state: string | null,
  balance: number | null
}

export interface ClienteCompleto {
  cpf: string;
  nome: string;
  email: string;
  salario: number;
  logradouro: string;
  numero: number;
  complemento: string;
  cep: number;
  cidade: string;
  estado: string;
  bairro:string;   
  conta: string; 
  saldo: number;
  limite: number;
  gerente: string; 
  gerente_nome: string;
  gerente_email: string;
}