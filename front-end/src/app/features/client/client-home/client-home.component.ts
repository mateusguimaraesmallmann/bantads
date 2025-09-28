import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { User } from '../../../core/models/user.model';
import { AuthService } from '../../../core/services/authentication/auth.service';
import { MoneyPipe } from '../../../shared/pipes/pipe-money';
import { AccountService } from '../../../core/services/account.service';
import { Account } from '../../../core/models/account.model';

@Component({
  selector: 'app-client-home',
  standalone: true,
  imports: [CommonModule, MoneyPipe],
  templateUrl: './client-home.component.html',
  styleUrl: './client-home.component.css'
})
export class ClientHomeComponent implements OnInit {
  currentUser: User | null = null;
  currentAccount: Account | null = null;

  constructor (private authService: AuthService, private accountService: AccountService, private router: Router){

  }

  ngOnInit(): void{
    this.currentUser = this.authService.getCurrentUser();

    if (this.currentUser && this.currentUser.cpf){
      this.accountService.returnAccountData(this.currentUser.cpf).subscribe({
        next: (response) => {
          this.currentAccount = response;
          console.log("Carregou os dados da conta!", response, this.currentAccount);
        },
        error: (err) => {
          console.error("Erro: ", err);
        }
      })
  }
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
}
