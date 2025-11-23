import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { User } from '../../../core/models/user.model';
import { AuthService } from '../../../core/services/authentication/auth.service';
import { MoneyPipe } from '../../../shared/pipes/pipe-money';
import { AccountService } from '../../../core/services/account.service';
import { Account, ClienteCompleto } from '../../../core/models/account.model';
import { UserService } from '../../../core/services/user.service';
import { ClientUpdate } from '../../../core/models/client-update.model';

@Component({
  selector: 'app-client-home',
  standalone: true,
  imports: [CommonModule, MoneyPipe],
  templateUrl: './client-home.component.html',
  styleUrl: './client-home.component.css',
})
export class ClientHomeComponent implements OnInit {
  infos: any;
  clienteCompleto?: ClienteCompleto ;

  constructor (private authService: AuthService, private router: Router, private userService : UserService){

  }

  ngOnInit(): void{
    this.infos = this.authService.getCurrentUser();
    this.processarInformacoes();
}

   options = [
  { name: 'Alteração de Perfil', route: '/update-profile' },
  { name: 'Depositar', route: '/deposit' },
  { name: 'Saque', route: '/withdraw' },
  { name: 'Transferência', route: '/transfer' },
  { name: 'Consulta de Extrato', route: '/transaction-history' }
];

action(option: any) {
  if (option.route) {
    this.router.navigate([option.route]);
  } else {
    alert(`Você clicou em: ${option.name}`);
  }
}

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  processarInformacoes(){
    this.userService.consultarClienteSaga(this.infos.cpf).subscribe({
      next: (response:ClienteCompleto) => {
        console.log("Carregou os dados da conta!", response);
        this.clienteCompleto = response;
      },
      error: (err:ClienteCompleto) => {
        console.error("Erro: ", err);
      }
    })
  }
}