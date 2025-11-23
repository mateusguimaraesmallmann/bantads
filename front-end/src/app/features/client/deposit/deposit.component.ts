import { MoneyPipe } from './../../../shared/pipes/pipe-money';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/authentication/auth.service';
import { TransactionService } from '../../../core/services/transaction.service';

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
  infos: any;
  erroDeposito: string = '';

  constructor(private router: Router, private authService: AuthService, private transationService: TransactionService) {}
  
  ngOnInit(): void {
    this.infos = this.authService.getCurrentUser();
  }

  onSubmit(): void {
    if (this.valorDeposito <= 0 || isNaN(this.valorDeposito)) {
      this.mensagem = 'Por favor, insira um valor válido.';
      return;
    }

    this.mensagem = '';
    this.valorFormatado = this.valorDeposito.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    });

    const confirmModalEl = document.getElementById('confirmDepositModal');
    if (confirmModalEl) {
      const confirmModal = new bootstrap.Modal(confirmModalEl);
      confirmModal.show();
    }

    console.log('Depósito de:', this.valorFormatado);
  }

  confirmarDeposito(): void {
    const confirmModalEl = document.getElementById('confirmDepositModal');
    if (confirmModalEl) {
      const confirmModal = bootstrap.Modal.getInstance(confirmModalEl);
      confirmModal?.hide();
    }
    this.processarDeposito();
  }

  processarDeposito() {
    this.erroDeposito = ''; 

    this.transationService.depositar(this.infos.conta, this.valorDeposito).subscribe({
      next: (response: any) => {
        console.log("Deposito realizado!", response);
        const successModalEl = document.getElementById('depositModal');
        if (successModalEl) {
          const successModal = new bootstrap.Modal(successModalEl);
          successModal.show();
        }
      },
      error: (err: any) => {
        console.error("Erro no depósito:", err);
        this.erroDeposito = "Erro ao realizar depósito. Tente novamente.";
        
        if (err.error && err.error.message) {
            this.erroDeposito = err.error.message;
        }
      }
    })
  }

  formatarValor(event: any): void {
    let valor = event.target.value.replace(/\D/g, '');
    let numero = parseFloat((parseInt(valor, 10) / 100).toFixed(2));

    event.target.value = numero.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    });

    this.valorDeposito = numero;
    this.valorFormatado = event.target.value;
  }

  voltar(): void {
    this.router.navigate(['/client-home']);
  }
}