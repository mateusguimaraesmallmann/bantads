export interface ItemExtratoResponse {
  data: string; 
  tipo: 'TRANSFERENCIA' | 'DEPOSITO' | 'SAQUE';
  origem: string | null;
  destino: string | null;
  valor: number; 
}

export interface ExtratoResponse {
  conta: string;
  saldo: number;
  movimentacoes: ItemExtratoResponse[];
}

export interface Transacoes {
  dataHora: string;
  operacao: string;
  tipo: 'entrada' | 'saida';
  cliente: string | null;
  valor: number;
}

export interface TodasTransacoes {
  data: string;
  saldoConsolidado: number;
  transacoes: Transacoes[];
}