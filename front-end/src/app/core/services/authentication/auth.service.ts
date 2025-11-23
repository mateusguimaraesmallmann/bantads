import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, throwError } from 'rxjs';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Login } from '../../models/login.model';
import { User } from '../../models/user.model';

const BASE_URL = "http://localhost:3000"


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  //httpOptions = {
      //observe: "response" as "response",
      //headers: new HttpHeaders({
        //'Content-Type': 'application/json'
      //}),

  private users = [
    { email: 'admin@bantads.com.br', password: '123456', role: 'ADMIN' },
    { email: 'ana.lima@bantads.com.br', password: '123456', role: 'MANAGER' },
    { email: 'paulo.silva@example.com', password: '123456', role: 'CLIENT' },
  ];

  constructor(private http: HttpClient) { }

  doLogin(login: Login): Observable<any> {
      return this.http.post<any>(`${BASE_URL}/login`, {
          login: login.email,
          senha: login.password
      }).pipe(
          map(response => {
              if (response && response.auth && response.token) {
                  localStorage.setItem('token', response.data.token);
                  localStorage.setItem('user', JSON.stringify(response.data));
                  return response.data;
              }
              alert("Resposta inválida do servidor.")
              return throwError(() => new Error('Resposta inválida do servidor'));
          }),
      );
  }

  logout() {
    localStorage.removeItem('user');
  }

  getCurrentUser() : User | null{
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }
}

