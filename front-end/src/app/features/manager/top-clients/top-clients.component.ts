import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../../core/components/header/header.component';
import { NgxMaskPipe, provideNgxMask } from 'ngx-mask';
import { NAVITEMS } from '../navItems';
import { AccountService } from '../../../core/services/account.service';
import { UserService } from '../../../core/services/user.service';
import { Account, topClientAccount } from '../../../core/models/account.model';
import { ClientDetails } from '../../../core/models/client-details.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-top-clients',
  imports: [CommonModule, HeaderComponent, NgxMaskPipe],
  providers: [
    provideNgxMask()
  ],
  templateUrl: './top-clients.component.html',
  styleUrl: './top-clients.component.css'
})
export class TopClientsComponent implements OnInit {
    navItems = NAVITEMS;

    constructor(private accountService: AccountService, private userService: UserService){

    }

    //Carrega os dados das contas e clientes e junta para a interface topClientAccount
    allAccounts: Account[] = [];
    allUsers: ClientDetails[] = [];

    topClients: topClientAccount[] = []

  ngOnInit(){
      forkJoin({
        accounts: this.accountService.returnAllAccounts(),
        clients: this.userService.returnAllClients()
      }).subscribe({
        next: ({accounts, clients}) => {

          // 1. Cria Mapa: ID do Cliente -> Dados do Cliente
          const clientMap = new Map<number, ClientDetails>();
          clients.forEach(client => {
             if(client.id) clientMap.set(client.id, client);
          });
          this.topClients = accounts
            // 2. Filtra apenas contas ATIVAS

            .map(account => {
               // 3. Busca dados do cliente usando o ID (idCliente vem do ms-conta)
               console.log(this.topClients)
               const clientData = clientMap.get(account.clientId);
               if (clientData) {
                 return {
                   cpf: clientData.cpf,
                   clientName: clientData.nome,
                   city: clientData.cidade || 'N/A', // Proteção contra null
                   state: clientData.estado || 'N/A',
                   balance: account.balance
                 };
               }
               return null;
            })
            // Remove os nulos (casos onde não achou o cliente)
            .filter((item): item is topClientAccount => item !== null)
            // 4. Ordena pelo Saldo Decrescente e pega os 3 primeiros
            .sort((a, b) => b.balance - a.balance)
            .slice(0, 3);
        },
        error: (err) => console.error("Erro ao carregar top clients: ", err)
      });
    }
}
