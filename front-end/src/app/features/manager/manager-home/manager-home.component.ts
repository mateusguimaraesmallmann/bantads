import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxMaskPipe, provideNgxMask } from 'ngx-mask';
import { HeaderComponent } from '../../../core/components/header/header.component';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { AproveClientComponent } from './components/aprove-client/aprove-client/aprove-client.component';
import { DenyClientComponent } from './components/deny-client/deny-client.component';

@Component({
  selector: 'app-manager-home',
  imports: [CommonModule, HeaderComponent, NgxMaskPipe],
  providers: [
    provideNgxMask(),
    BsModalService,
  ],
  templateUrl: './manager-home.component.html',
  styleUrl: './manager-home.component.css'
})
export class ManagerHomeComponent {

    constructor(private modalService: BsModalService){}
    bsModalRef?: BsModalRef;

    solicitacoes = [
      {cpf: '12345678910', nome: "Tiago Salles", salario: 1500},
      {cpf: '12345678910', nome: "Tiago Salles", salario: 1500},
      {cpf: '12345678910', nome: "Tiago Salles", salario: 1500}
    ]

    numPedidos: number = this.solicitacoes.length;

    //TODO: Com os modais implementados, falta deixar a retirada das contas e alteração no card de pedidos dinâmica
    responseManager(id: String){
        if (id === "aproveButton"){
          this.bsModalRef = this.modalService.show(AproveClientComponent);
        }else{
          this.bsModalRef = this.modalService.show(DenyClientComponent);
        }
    }
}
