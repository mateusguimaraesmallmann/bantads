import { Injectable} from '@angular/core';
import { ClientDetails } from '../models/client-details.model';
import { HttpClient } from '@angular/common/http';
import { Observable} from 'rxjs';

const CLIENTES_URL = 'http://localhost:3000/clientes';

@Injectable({ providedIn: 'root' })
export class AdministratorService {

  constructor(private http: HttpClient){}

  listarTodosOsClientesAdministrator(): Observable<ClientDetails[]>{
      const url = `${CLIENTES_URL}?filtro=adm_relatorio_clientes`;
      return this.http.get<ClientDetails[]>(url);
  }

}
