import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { ExtratoResponse } from '../models/transaction-history.model';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  private apiUrl = '';

  constructor(private http: HttpClient) { }

  consultarExtratoPorCpf(cpfUsuario:string):Observable<ExtratoResponse>{
    const url = `${this.apiUrl}/${cpfUsuario}`;
    return this.http.get<ExtratoResponse>(url);
  }

}