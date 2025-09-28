import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MoneyPipe } from '../../../shared/pipes/pipe-money';

@Component({
  selector: 'app-client-home',
  standalone: true,
  imports: [CommonModule, MoneyPipe],
  templateUrl: './client-home.component.html',
  styleUrl: './client-home.component.css'
})
export class ClientHomeComponent {
  saldo: number = 1150.75; //exemplo
  nomeCliente: string = 'Paulo';

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
    this.router.navigate(['/login']);
  }
}
