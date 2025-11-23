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
  valor: number;
}

export interface TodasTransacoes {
  data: string;
  saldoConsolidado: number;
  transacoes: Transacoes[];
}

export interface OperacaoContaRequest {
  valor: number;
}

export interface TransferenciaRequest {
  destino: string;
  valor: number;
}

export interface SaldoResponse {
    cliente: string;
    conta: string;
    saldo: number;
}

export interface OperacaoResponse {
    conta: string;
    data: string;
    saldo: number;
}
