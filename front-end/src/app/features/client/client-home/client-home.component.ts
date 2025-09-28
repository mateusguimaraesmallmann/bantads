import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { User } from '../../../core/models/user.model';
import { AuthService } from '../../../core/services/authentication/auth.service';
import { MoneyPipe } from '../../../shared/pipes/pipe-money';

@Component({
  selector: 'app-client-home',
  standalone: true,
  imports: [CommonModule, MoneyPipe],
  templateUrl: './client-home.component.html',
  styleUrl: './client-home.component.css'
})
export class ClientHomeComponent implements OnInit {
  saldo: number = 0;
  currentUser: User | null = null;

  constructor (private authService: AuthService){

  }

  ngOnInit(): void{
    this.currentUser = this.authService.getCurrentUser();

    if (this.currentUser) {
      this.saldo = this.currentUser.balance ?? 0;
    }
  }

  private router = inject(Router);

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
