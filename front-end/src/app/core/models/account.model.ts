export interface Account {
  number: number;
  clientId: number;
  clientName: string | null;
  balance: number;
  limit: number;
  managerCpf: string | null;
  managerName: string | null;
  createdAt: Date;
  status: string;
}


//Interface para carregar os clientes com mais saldo na conta
export interface topClientAccount{
  clientId: number,
  cpf: string,
  clientName: string,
  city: string,
  state: string,
  balance: number
  status: string
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
