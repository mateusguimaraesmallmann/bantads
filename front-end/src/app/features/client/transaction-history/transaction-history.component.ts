import { Component, OnInit } from '@angular/core';
import { CommonModule,DatePipe  } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {ItemExtratoResponse,ExtratoResponse,Transacoes,TodasTransacoes} from '../../../core/models/transaction-history.model';
import { Router } from '@angular/router';
import { MoneyPipe } from '../../../shared/pipes/pipe-money';
import { TransactionService } from '../../../core/services/transaction.service';
import { AuthService } from '../../../core/services/authentication/auth.service';

@Component({
  selector: 'app-transaction-history',
  imports: [
    CommonModule,
    FormsModule,
    MoneyPipe],
  providers: [DatePipe],
  templateUrl: './transaction-history.component.html',
  styleUrl: './transaction-history.component.css'
})

export class TransactionHistoryComponent implements OnInit{
  public dataInicial: string = '';
  public dataFinal: string = '';
  public dadosCarregados:boolean = false;

  public numeroDaConta: string | null = null;
  public balanco: number = 0 ;
  public listaDeTransacoes: TodasTransacoes[] = [];
  public isLoading: boolean = false;
  public hasSearched: boolean = false;
  infos : any;

  constructor(private datePipe: DatePipe, private router: Router, private transactionHistory: TransactionService, private authService:AuthService) {}

  ngOnInit(): void {
    this.infos = this.authService.getCurrentUser();
    this.definirDatasPadrao();
    this.buscarExtratos();
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
    this.transactionHistory.consultarExtratoPorCpf(this.infos.conta).subscribe({
    next: (response) => this.listarTransacoes(response),
    error: (err) => console.log(err)
    });
    this.isLoading = false;
  }


  private listarTransacoes(response: ExtratoResponse): void {
    this.numeroDaConta = response.conta;
    this.balanco = response.saldo;
    this.dadosCarregados = true;

    if (response.movimentacoes.length === 0) {
      this.listaDeTransacoes = [];
      this.isLoading = false;
      return;
    }

    const diasAgrupados: { dataChave: string, transacoes: Transacoes[] }[] = [];

    for (const movimentacao of response.movimentacoes) {
      const dataOriginal = movimentacao.data;
      const dataChave = dataOriginal.split('T')[0]; 
      
      let diaExistente = diasAgrupados.find(dia => dia.dataChave === dataChave);

      if (!diaExistente) {
        diaExistente = { dataChave: dataChave, transacoes: [] };
        diasAgrupados.push(diaExistente);
      }

      const ehSaida = movimentacao.origem === this.infos.conta;

      const transacao: Transacoes = {
        dataHora: this.datePipe.transform(movimentacao.data, 'dd/MM/yyyy HH:mm:ss') || '',
        valor: movimentacao.valor,
        tipo: ehSaida ? 'saida' : 'entrada',
        operacao: movimentacao.tipo.toLocaleUpperCase()
      };

      diaExistente.transacoes.push(transacao);
    }
    const diasOrdenados = diasAgrupados.sort((a, b) => b.dataChave.localeCompare(a.dataChave));
    
    let saldoNoFinalDoDia = response.saldo;
    const extratosCalculados: TodasTransacoes[] = [];

    for (const dia of diasOrdenados) {
      const transacoesDoDia = dia.transacoes;
      let variacaoDoDia = 0;

      for (const t of transacoesDoDia) {
        if (t.tipo === 'entrada') {
          variacaoDoDia += t.valor;
        } else {
          variacaoDoDia -= t.valor;
        }
      }
      
      transacoesDoDia.sort((a, b) => {
        const [dataA, horaA] = a.dataHora.split(' ');
        const [diaA, mesA, anoA] = dataA.split('/');
        const dataObjA = new Date(`${anoA}-${mesA}-${diaA}T${horaA}`);

        const [dataB, horaB] = b.dataHora.split(' ');
        const [diaB, mesB, anoB] = dataB.split('/');
        const dataObjB = new Date(`${anoB}-${mesB}-${diaB}T${horaB}`);

        return dataObjB.getTime() - dataObjA.getTime();
      });

      if (dia.dataChave >= this.dataInicial && dia.dataChave <= this.dataFinal) {
        extratosCalculados.push({
          data: this.datePipe.transform(dia.dataChave, 'dd/MM/yyyy', 'UTC') || '',
          saldoConsolidado: saldoNoFinalDoDia,
          transacoes: transacoesDoDia
        });
      }
      saldoNoFinalDoDia -= variacaoDoDia;
    }

    this.listaDeTransacoes = extratosCalculados;
    this.isLoading = false;
  }

  //identifica o cliente da operação e retorna
  //se ele é originário ou destinatário em caso de transferência
  private formatarCliente(movimentacao: ItemExtratoResponse): string | null {
    if (movimentacao.tipo === 'TRANSFERENCIA') {
      return movimentacao.valor > 0 ? movimentacao.origem : movimentacao.destino;
    }
    return null;
  }
  
  processarSucesso(callback: () => void) {
    console.log("deu boa");
    callback();
  }  

  processarErro(error: any): void {
    console.log("erro ao processar solicitação")
  }

  //retorna o cliente para sua tela inicial
  voltar(): void {
    this.router.navigate(['/client-home']);
  }
}
