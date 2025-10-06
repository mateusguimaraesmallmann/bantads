import { Component, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal'
import { SolicitacoesService } from '../../../../../../core/services/solicitacoes.service';

@Component({
  selector: 'app-aprove-client',
  imports: [],
  templateUrl: './aprove-client.component.html',
  styleUrl: './aprove-client.component.css'
})
export class AproveClientComponent{

  cpfCliente: string = '';
  requestSuccess: boolean = false;

  constructor(public bsModalRef: BsModalRef, private solicitacoesService: SolicitacoesService) {}

  closeModal(){
    this.bsModalRef.hide();
  }

  approveRequest(){
    this.solicitacoesService.approveRequest(this.cpfCliente).subscribe({
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
