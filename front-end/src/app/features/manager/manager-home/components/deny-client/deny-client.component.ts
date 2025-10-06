import { Component } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { SolicitacoesService } from '../../../../../core/services/solicitacoes.service';
@Component({
  selector: 'app-deny-client',
  imports: [],
  templateUrl: './deny-client.component.html',
  styleUrl: './deny-client.component.css'
})
export class DenyClientComponent {

  cpfCliente: string = '';
  requestSuccess: boolean = false;

  constructor(public bsModalRef: BsModalRef, private solicitacoesService: SolicitacoesService){}

  closeModal(){
    this.bsModalRef.hide();
  }

  denyRequest(){
    this.solicitacoesService.denyRequest(this.cpfCliente).subscribe({
      next: (response) => {
        this.requestSuccess = true;
        this.bsModalRef.hide();
      },
      error: (err) =>{
        console.error('Erro');
      }
    });
    this.bsModalRef.hide();
  }
}
