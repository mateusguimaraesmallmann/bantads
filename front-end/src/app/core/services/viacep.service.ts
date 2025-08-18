import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface ViaCepResponse {
  cep: string;
  logradouro: string;
  complemento: string;
  bairro: string;
  localidade: string;
  uf: string;
  ibge?: string;
  gia?: string;
  ddd?: string;
  siafi?: string;
  erro?: boolean;
}

@Injectable({providedIn: 'root'})
export class ViacepService {
  constructor(private http: HttpClient) {}

  getByCep(cepRaw: string): Observable<ViaCepResponse> {
    const cep = (cepRaw || '').replace(/\D/g, '');
    return this.http.get<ViaCepResponse>(`https://viacep.com.br/ws/${cep}/json/`).pipe(map(res => res));
  }

}
