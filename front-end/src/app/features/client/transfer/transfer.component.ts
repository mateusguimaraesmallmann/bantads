import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MoneyPipe } from "../../../shared/pipes/pipe-money";
import { AuthService } from '../../../core/services/authentication/auth.service';
import { TransactionService } from '../../../core/services/transaction.service';

declare var bootstrap: any;

@Component({
  selector: 'app-transfer',
  imports: [FormsModule, CommonModule, MoneyPipe],
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css']
})
export class TransferComponent {
  contaDestino: string = '';
  valorTransferencia: number = 0;
  valorFormatado: string = '';
  dataHora: Date = new Date();

  mensagemValor: string = '';
  infos:any;
  erroTransferencia: string = '';
  constructor(private router: Router, private authService: AuthService, private transationService:TransactionService) {}

  ngOnInit(): void{
    this.infos = this.authService.getCurrentUser();
  }

  onSubmit(form: any): void {
    if (!form.valid) return;

    if (this.valorTransferencia <= 0 || isNaN(this.valorTransferencia)) {
      this.mensagemValor = 'Por favor, insira um valor válido.';
      return;
    }

    if (!this.infos || (this.infos.saldo ?? 0) < this.valorTransferencia) {
      this.mensagemValor = 'Saldo insuficiente.';
      return;
    }

    this.mensagemValor = '';
    this.valorFormatado = this.valorTransferencia.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    });
    this.dataHora = new Date();

    const modalElement = document.getElementById('transferModal');
    if (modalElement) {
      const modal = new bootstrap.Modal(modalElement);
      modal.show();
    }
    const confirmModalEl = document.getElementById('confirmTransferModal');
    if (confirmModalEl) {
      const confirmModal = new bootstrap.Modal(confirmModalEl);
      confirmModal.show();
    }
  }

  confirmarTransferencia(): void {
  const confirmModalEl = document.getElementById('confirmTransferModal');
    if (confirmModalEl) {
      const confirmModal = bootstrap.Modal.getInstance(confirmModalEl);
      confirmModal?.hide();
    }
    this.processarTransferencia();
  }
  
  processarTransferencia(){
    this.transationService.transferir(this.infos.conta,this.contaDestino, this.valorTransferencia).subscribe({
      next: (response: any) => {
        console.log("Transferencia realizada!", response);
        this.infos.saldo = (this.infos.saldo ?? 0) - this.valorTransferencia;
        localStorage.setItem('user', JSON.stringify(this.infos));

        const successModalEl = document.getElementById('successTransferModal');
        if (successModalEl) {
          const successModal = new bootstrap.Modal(successModalEl);
          successModal.show();
        }
      },
      error: (err: any) => {
      console.error("Erro na transferência:", err);
      this.erroTransferencia = "Erro ao realizar transferência. Verifique os dados e tente novamente.";
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

    this.valorTransferencia = numero;
    this.valorFormatado = event.target.value;
  }

  voltar(): void {
    this.contaDestino = '';
    this.valorTransferencia = 0;
    this.valorFormatado = '';
    this.router.navigate(['/client-home']);
  }
}

