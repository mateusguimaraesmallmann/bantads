import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { ExtratoResponse, OperacaoContaRequest, TransferenciaRequest, SaldoResponse, OperacaoResponse } from '../models/transaction-history.model';

@Injectable({
  providedIn: 'root'
})

export class TransactionService {

  private apiUrl = 'http://localhost:3000/contas';

  constructor(private http: HttpClient) { }

  //consultarExtratoPorCpf(numeroConta:string):Observable<ExtratoResponse>{
  //  const url = `${this.apiUrl}/${numeroConta}/extrato`;
  //  return this.http.get<ExtratoResponse>(url);
  //}

  consultarSaldo(numeroConta: string): Observable<SaldoResponse> {
    const url = `${this.apiUrl}/${numeroConta}/saldo`;
    return this.http.get<SaldoResponse>(url);
  }

  consultarExtrato(numeroConta: string): Observable<ExtratoResponse>{
    const url = `${this.apiUrl}/${numeroConta}/extrato`;
    return this.http.get<ExtratoResponse>(url);
  }

  depositar(numeroConta: string, valor: number): Observable<OperacaoResponse> {
    const url = `${this.apiUrl}/${numeroConta}/depositar`;
    const payload: OperacaoContaRequest = { valor: valor };
    return this.http.post<OperacaoResponse>(url, payload);
  }

  sacar(numeroConta: string, valor: number): Observable<OperacaoResponse> {
    const url = `${this.apiUrl}/${numeroConta}/sacar`;
    const payload: OperacaoContaRequest = { valor: valor };
    return this.http.post<OperacaoResponse>(url, payload);
  }

  transferir(numeroContaOrigem: string, numeroContaDestino: string, valor: number): Observable<OperacaoResponse> {
    const url = `${this.apiUrl}/${numeroContaOrigem}/transferir`;
    const payload: TransferenciaRequest = {
        destino: numeroContaDestino,
        valor: valor
    };
    return this.http.post<OperacaoResponse>(url, payload);
  }
}
