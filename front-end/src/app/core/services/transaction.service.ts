import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { ExtratoResponse } from '../models/transaction-history.model';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  private apiUrl = 'http://localhost:3000/contas';

  constructor(private http: HttpClient) { }

  consultarExtratoPorCpf(numeroConta:string):Observable<ExtratoResponse>{
    const url = `${this.apiUrl}/${numeroConta}/extrato`;
    return this.http.get<ExtratoResponse>(url);
  }

}
