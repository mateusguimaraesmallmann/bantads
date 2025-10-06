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
import { AuthService } from '../../../core/services/authentication/auth.service';
import { User } from '../../../core/models/user.model';
import { HttpClient } from '@angular/common/http';
import { take } from 'rxjs';

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
    currentUser: User | null = null;

    constructor(private modalService: BsModalService, private solicitacoesService: SolicitacoesService, private authService: AuthService){}
    bsModalRef?: BsModalRef;

    navItems = NAVITEMS;

    solicitacoes: ClientDetailsCpf[] = [];

    ngOnInit(): void{
      this.currentUser = this.authService.getCurrentUser();
      this.solicitacoesService.listRequests().subscribe({
        next: (data) =>{
          this.solicitacoes = data;
        },
        error: (err) =>{
          console.error('Erro ao buscar as solicitações: ', err);
        }
      });
    }

    responseManager(id: String, cpfCliente: string){

      const modalConfig = {
        initialState: {
          cpfCliente: cpfCliente,
        }
      };

        if (id === "aproveButton"){
          this.bsModalRef = this.modalService.show(AproveClientComponent, modalConfig);
        }else{
          this.bsModalRef = this.modalService.show(DenyClientComponent, modalConfig);
        }

        //Cria um listener que recebe uma callback quando o método onHidden do modal é chamado
        //Se a operação do modal foi concluída, sem somente fechá-lo, atualiza a lista de solicitações
        if (this.bsModalRef?.onHidden){
          this.bsModalRef.onHidden.pipe(take(1)).subscribe(() => {
            const success = this.bsModalRef?.content.requestSuccess;

            if (success){
              this.updateSolicitationList(cpfCliente);
            }
          })
        }
    }

    //Método para atualizar a lista dinamicamente na tela
      public updateSolicitationList(cpf: string){
          const client = this.solicitacoes.find((cliente) => cliente.cpf === cpf);
          const arrayIndex = this.solicitacoes.indexOf(client!);
          this.solicitacoes.splice(arrayIndex, 1)
      }
}
