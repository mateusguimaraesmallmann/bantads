import { Injectable} from '@angular/core';
import { ClientDetails } from '../models/client-details.model';
import { HttpClient } from '@angular/common/http';
import { Observable} from 'rxjs';

const ADMINISTRATOR_URL = 'http://localhost:3000/administrator';

@Injectable({ providedIn: 'root' })
export class AdministratorService {

  constructor(private http: HttpClient){}

  listarTodosOsClientesAdministrator(): Observable<ClientDetails[]>{
      const url = ADMINISTRATOR_URL + "/clientes";
      return this.http.get<ClientDetails[]>(url);  
  }

}
