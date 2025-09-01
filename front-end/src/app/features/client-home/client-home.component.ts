import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-client-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './client-home.component.html',
  styleUrl: './client-home.component.css'
})
export class ClientHomeComponent {
  saldo: number = -150.75; //exemplo
  nomeCliente: string = 'João';

  private router = inject(Router);

  opcoes = [
    { nome: 'Alteração de Perfil' },
    { nome: 'Depositar' },
    { nome: 'Saque' },
    { nome: 'Transferência' },
    { nome: 'Consulta de Extrato' }
  ];

  acao(opcao: any) {
  if (opcao.nome === 'Alteração de Perfil') {
    this.router.navigate(['/update-profile']);
  } else if (opcao.nome === 'Depositar') {
    this.router.navigate(['/deposit']);
  } else {
    alert(`Você clicou em: ${opcao.nome}`);
  }
}

  logout() {
    this.router.navigate(['/login']);
  }
}
