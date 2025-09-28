import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MoneyPipe } from '../../../shared/pipes/pipe-money';
import { AuthService } from '../../../core/services/authentication/auth.service';

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

  constructor(private router: Router, private authService: AuthService) {}

  onSubmit(): void {
    if (this.valorSaque <= 0 || isNaN(this.valorSaque)) {
      this.mensagem = 'Por favor, insira um valor válido.';
      return;
    }

    const currentUser = this.authService.getCurrentUser();
    if (!currentUser || (currentUser.balance ?? 0) < this.valorSaque) {
      this.mensagem = 'Saldo insuficiente.';
      return;
    }

    this.mensagem = '';
    this.valorFormatado = this.valorSaque.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    });

    const confirmModalEl = document.getElementById('confirmWithdrawModal');
    if (confirmModalEl) {
      const confirmModal = new bootstrap.Modal(confirmModalEl);
      confirmModal.show();
    }

    console.log('Saque de:', this.valorFormatado);
  }

  confirmarSaque(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      currentUser.balance = (currentUser.balance ?? 0) - this.valorSaque;
      localStorage.setItem('user', JSON.stringify(currentUser));
    }

    const confirmModalEl = document.getElementById('confirmWithdrawModal');
    if (confirmModalEl) {
      const confirmModal = bootstrap.Modal.getInstance(confirmModalEl);
      confirmModal?.hide();
    }

    const successModalEl = document.getElementById('successWithdrawModal');
    if (successModalEl) {
      const successModal = new bootstrap.Modal(successModalEl);
      successModal.show();
    }
  }

  formatarValor(event: any): void {
    let valor = event.target.value.replace(/\D/g, '');
    let numero = parseFloat((parseInt(valor, 10) / 100).toFixed(2));

    event.target.value = numero.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    });

    this.valorSaque = numero;
    this.valorFormatado = event.target.value;
  }

  voltar(): void {
    this.valorSaque = 0;
    this.valorFormatado = '';
    this.router.navigate(['/client-home']);
  }
}


