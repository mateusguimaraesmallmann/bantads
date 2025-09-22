import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxMaskPipe, provideNgxMask } from 'ngx-mask';
import { HeaderComponent } from '../../../core/components/header/header.component';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { AproveClientComponent } from './components/aprove-client/aprove-client/aprove-client.component';
import { DenyClientComponent } from './components/deny-client/deny-client.component';
import { NAVITEMS } from '../navItems';
import { ClientDetailsCpf } from '../../../core/models/client-details.model';
import { SolicitacoesService } from '../../../core/services/solicitacoes.service';

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
export class ManagerHomeComponent implements OnInit {

    constructor(private modalService: BsModalService, private solicitacoesService: SolicitacoesService){}
    bsModalRef?: BsModalRef;

    navItems = NAVITEMS;

    solicitacoes: ClientDetailsCpf[] = [];

    numPedidos: number = 0;

    ngOnInit(): void{
      this.solicitacoesService.listRequests().subscribe({
        next: (data) =>{
          this.solicitacoes = data;
          this.numPedidos = this.solicitacoes.length;
        },
        error: (err) =>{
          console.error('Erro ao buscar as solicitações: ', err);
        }
      });
    }

    //TODO: Com os modais implementados, falta deixar a retirada das contas e alteração no card de pedidos dinâmica

    responseManager(id: String, cpfCliente: string){

      const modalConfig = {
        initialState: {
          cpfCliente: cpfCliente
        }
      };

        if (id === "aproveButton"){
          this.bsModalRef = this.modalService.show(AproveClientComponent, modalConfig);
        }else{
          this.bsModalRef = this.modalService.show(DenyClientComponent, modalConfig);
        }
    }
}
