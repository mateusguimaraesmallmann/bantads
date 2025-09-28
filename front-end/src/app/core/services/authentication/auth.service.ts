import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, throwError } from 'rxjs';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Login } from '../../models/login.model';
import { User } from '../../models/user.model';

const BASE_URL = "http://localhost:3000/users"


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

  //doLogin(login: Login) : Observable<any>{
  //console.log(login)
  //return this.http.post<Login>(BASE_URL,
    //JSON.stringify(login),
    //this.httpOptions).pipe(
      //map((resp: HttpResponse<Login>) => {
        //if(resp != null){
          //console.log(resp.body)
          //return resp.body
        //}else{
          //return null;
        //}
      //}),
      //catchError((err) => {
        //return throwError(() => err);
      //}))
  //}

  doLogin(login: Login): Observable<any> {
    return this.http.get<User[]>(`${BASE_URL}?email=${login.email}&password=${login.password}`).pipe(
      map(users => {
        if (users && users.length > 0){
          const user = users[0];
          if (user.status != "ACTIVE"){
            return alert("Usuário desativado ou ainda não foi aprovado.");
          }
          localStorage.setItem('user', JSON.stringify(user));
          return user;
        }
        alert("Usuário ou senha inválidos!")
        return throwError(() => new Error('Usuário ou senha inválidos'));
      })
    )
  }

  logout() {
    localStorage.removeItem('user');
  }

  getCurrentUser() : User | null{
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }
}

