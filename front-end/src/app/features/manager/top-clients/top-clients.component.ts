import { Component } from '@angular/core';
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
export class TopClientsComponent {
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
          const clientMap = new Map<string, ClientDetails>();
          for (const client of clients){
            clientMap.set(client.cpf, client);
          }

          this.topClients = accounts.map(account => {
            //Bate os dados do CPF da conta com os obtidos da tabela de clientes
            const clientData = clientMap.get(account.clientCpf!);

            //Se não for vazio
            if (clientData){
              return {
                cpf: account.clientCpf,
                clientName: account.clientName,
                city: clientData.cidade,
                state: clientData.estado,
                balance: account.balance
              }
            }

            return null;
          })
          .filter((client) => client != null)
          .sort((a, b) => b.balance - a.balance)
        },
        error: (err) => {
          console.log("Deu ruim pra carregar os melhores clientes: ", err);
        }
      })
    }
}
