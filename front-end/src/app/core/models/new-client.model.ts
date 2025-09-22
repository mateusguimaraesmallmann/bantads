export interface NewUser{
  nome: string | null;
  email: string | null;
  cpf: string | null;
  salario: number | null;
  cep: string | null;
  logradouro: string | null;
  numero: string | null;
  complemento?: string | null;
  bairro: string | null;
  cidade: string | null;
  estado: string | null;
  status: string;
  role: string;
}
