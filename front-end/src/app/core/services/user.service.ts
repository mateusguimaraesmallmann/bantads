import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs'; // Importa Observable
import { UserUpdate } from '../models/user-update.model';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  private apiUrl = '';

  constructor(private http: HttpClient) { }


  updateUser(user: UserUpdate): Observable<UserUpdate> {
    console.log(user);
    return of(user); 
  }
}