import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Login } from '../models/login.model';
const BASE_URL = "http://localhost:3000/login"

@Injectable({
  providedIn: 'root'
})
export class AuthService {

    httpOptions = {
      observe: "response" as "response",
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
  }

  constructor(private http: HttpClient) { }

  doLogin(login: Login) : Observable<any>{
  console.log(login)
  return this.http.post<Login>(BASE_URL,
    JSON.stringify(login),
    this.httpOptions).pipe(
      map((resp: HttpResponse<Login>) => {
        if(resp != null){
          console.log(resp.body)
          return resp.body
        }else{
          return null;
        }
      }),
      catchError((err) => {
        return throwError(() => err);
      }))    
  }

}
