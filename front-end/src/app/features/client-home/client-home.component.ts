import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-client-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './client-home.component.html',
  styleUrl: './client-home.component.css'
})
export class ClientHomeComponent {
  saldo: number = -150.75; //exemplo

  opcoes = [
    { nome: 'Alteração de Perfil' },
    { nome: 'Depositar' },
    { nome: 'Saque' },
    { nome: 'Transferência' },
    { nome: 'Consulta de Extrato' }
  ];

  acao(opcao: any) {
    alert(`Você clicou em: ${opcao.nome}`);
  }
}
