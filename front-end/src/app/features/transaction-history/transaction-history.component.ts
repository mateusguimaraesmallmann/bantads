import { Component, OnInit } from '@angular/core';
import { CommonModule,DatePipe  } from '@angular/common';
import { FormsModule } from '@angular/forms'; 
import {ItemExtratoResponse,ExtratoResponse,Transacoes,TodasTransacoes} from '../../core/models/transaction-history.model';

@Component({
  selector: 'app-transaction-history',
  imports: [
    CommonModule, 
    FormsModule],
  providers: [DatePipe],
  templateUrl: './transaction-history.component.html',
  styleUrl: './transaction-history.component.css'
})

export class TransactionHistoryComponent implements OnInit{
  public dataInicial: string = '';
  public dataFinal: string = '';

  public numeroDaConta: string | null = null;
  public balanco: number | null = null;
  public listaDeTransacoes: TodasTransacoes[] = [];
  public isLoading: boolean = false;
  public hasSearched: boolean = false;

  constructor(private datePipe: DatePipe) {}

  ngOnInit(): void {
    this.definirDatasPadrao();
  }

  private definirDatasPadrao(): void {
    const hoje = new Date();
    const trintaDiasAtras = new Date();
    trintaDiasAtras.setDate(hoje.getDate() - 30);

    this.dataFinal = this.formatarDataParaInput(hoje);
    this.dataInicial = this.formatarDataParaInput(trintaDiasAtras);
  }

  private formatarDataParaInput(data: Date): string {
    return this.datePipe.transform(data, 'yyyy-MM-dd') || '';
  }

  public buscarExtratos(): void {
    this.isLoading = true;
    this.hasSearched = true;
    this.listaDeTransacoes = []; 
    const mockApiResponse: ExtratoResponse = {
      conta: "8722",
      saldo: 1933.32,
      movimentacoes: [
        { data: "2025-08-24T09:15:00Z", tipo: "SAQUE", origem: null, destino: null, valor: -100.00 },
        { data: "2025-08-22T15:45:10Z", tipo: "TRANSFERENCIA", origem: "8722", destino: "Maria Silva", valor: -200.00 },
        { data: "2025-08-22T10:30:00Z", tipo: "DEPOSITO", origem: null, destino: null, valor: 500.00 },
        { data: "2025-08-20T11:00:00Z", tipo: "TRANSFERENCIA", origem: "João Souza", destino: "8722", valor: 1733.32 },
      ]
    };
    // this.transactionHistory.findTransactionHistory(userUpdate).subscribe({
    // next: (response) => this.handleSuccess(response),
    // error: (err) => this.handleError(err)
    // });   
    this.handleSuccess(mockApiResponse);
    this.isLoading = false;
  }

  private handleSuccess(response: ExtratoResponse): void {
    this.numeroDaConta = response.conta;
    this.balanco = response.saldo;

    if (response.movimentacoes.length === 0) {
      this.listaDeTransacoes = [];
      return;
    }

    const diasAgrupados: { dataChave: string, transacoes: Transacoes[] }[] = [];

    for (const movimentacao of response.movimentacoes) {
      const dataChave = movimentacao.data.split('T')[0];
      let diaExistente = diasAgrupados.find(dia => dia.dataChave === dataChave);

      if (!diaExistente) {
        diaExistente = { dataChave: dataChave, transacoes: [] };
        diasAgrupados.push(diaExistente);
      }

      const transacao: Transacoes = {
        dataHora: this.datePipe.transform(movimentacao.data, 'dd/MM/yyyy HH:mm:ss') || '',
        valor: movimentacao.valor,
        tipo: movimentacao.valor > 0 ? 'entrada' : 'saida',
        operacao: this.formatarOperacao(movimentacao),
        cliente: this.formatarCliente(movimentacao)
      };

      diaExistente.transacoes.push(transacao);
    }
    
    const diasOrdenados = diasAgrupados.sort((a, b) => b.dataChave.localeCompare(a.dataChave));
    let saldoAtual = response.saldo;
    const extratosCalculados: TodasTransacoes[] = [];

    for (const dia of diasOrdenados) {
      const transacoesDoDia = dia.transacoes;
      const totalDoDia = transacoesDoDia.reduce((soma, t) => soma + t.valor, 0);

      extratosCalculados.push({
        data: this.datePipe.transform(dia.dataChave, 'dd/MM/yyyy', 'UTC') || '',
        saldoConsolidado: saldoAtual,
        transacoes: transacoesDoDia.sort((a, b) => 
          new Date(b.dataHora.split(' ')[0].split('/').reverse().join('-') + 'T' + b.dataHora.split(' ')[1]).getTime() -
          new Date(a.dataHora.split(' ')[0].split('/').reverse().join('-') + 'T' + a.dataHora.split(' ')[1]).getTime()
        )
      });
      saldoAtual -= totalDoDia;
    }
    
    this.listaDeTransacoes = extratosCalculados.reverse();
    this.isLoading = false; 
  }
  
  private formatarOperacao(movimentacao: ItemExtratoResponse): string {
    if (movimentacao.tipo === 'TRANSFERENCIA') {
      return movimentacao.valor > 0 ? 'Transferência Recebida' : 'Transferência Enviada';
    }
    return movimentacao.tipo.charAt(0) + movimentacao.tipo.slice(1).toLowerCase(); 
  }

  private formatarCliente(movimentacao: ItemExtratoResponse): string | null {
    if (movimentacao.tipo === 'TRANSFERENCIA') {
      return movimentacao.valor > 0 ? movimentacao.origem : movimentacao.destino;
    }
    return null;
  }

  handleError(error: any): void {
    console.log("erro ao processar solicitação")
  }
}
