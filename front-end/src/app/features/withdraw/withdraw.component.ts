import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MoneyPipe } from "../../shared/pipes/pipe-money";

declare var bootstrap: any;

@Component({
  selector: 'app-withdraw',
  imports: [FormsModule, CommonModule, MoneyPipe],
  templateUrl: './withdraw.component.html',
  styleUrls: ['./withdraw.component.css']
})
export class WithdrawComponent {
  valorSaque: number = 0;
  valorFormatado: string = '';
  mensagem: string = '';

  constructor(private router: Router) {}

  onSubmit(): void {
    if (this.valorSaque > 0) {
      this.valorFormatado = this.valorSaque.toLocaleString('pt-BR', {
        style: 'currency',
        currency: 'BRL'
      });

      const modalElement = document.getElementById('withdrawModal');
      if (modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
      }

      console.log('Saque de:', this.valorSaque);
    } else {
      this.mensagem = 'Por favor, insira um valor válido para o saque.';
    }
  }

  confirmarSaque(): void {
    console.log(`Saque confirmado: ${this.valorFormatado}`);

    this.valorSaque = 0;
    this.valorFormatado = '';
  }

  formatarValor(event: any): void {
    let valor = event.target.value.replace(/\D/g, '');
    let numero = parseFloat((parseInt(valor, 10) / 100).toFixed(2));

    event.target.value = numero.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    });

    this.valorSaque = numero;
  }

  voltar(): void {
    this.router.navigate(['/client-home']);
  }
}
