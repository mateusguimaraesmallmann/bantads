import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MoneyPipe } from "../../shared/pipes/pipe-money";

declare var bootstrap: any;

@Component({
  selector: 'app-deposit',
  imports: [FormsModule, CommonModule, MoneyPipe],
  templateUrl: './deposit.component.html',
  styleUrls: ['./deposit.component.css']
})
export class DepositComponent {
  valorDeposito: number = 0;
  valorFormatado: string = '';
  mensagem: string = '';

  constructor(private router: Router) {}

  onSubmit(): void {
    if (this.valorDeposito > 0) {
      this.valorFormatado = this.valorDeposito.toLocaleString('pt-BR', {
        style: 'currency',
        currency: 'BRL'
      });

      const modalElement = document.getElementById('depositModal');
      if (modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
      }

      console.log('Depósito de:', this.valorDeposito);
    } else {
      this.mensagem = 'Por favor, insira um valor válido para o depósito.';
    }
  }

  confirmarDeposito(): void {
    console.log(`Depósito confirmado: ${this.valorFormatado}`);

    this.valorDeposito = 0;
    this.valorFormatado = '';
  }

  formatarValor(event: any): void {
    let valor = event.target.value.replace(/\D/g, '');
    let numero = parseFloat((parseInt(valor, 10) / 100).toFixed(2));

    event.target.value = numero.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    });

    this.valorDeposito = numero;
  }

  voltar(): void {
    this.router.navigate(['/client-home']);
  }
}

