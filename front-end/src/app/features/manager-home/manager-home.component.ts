import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxMaskPipe, provideNgxMask } from 'ngx-mask';
import { HeaderComponent } from '../../core/components/header/header.component';

@Component({
  selector: 'app-manager-home',
  imports: [CommonModule, HeaderComponent, NgxMaskPipe],
  providers: [
    provideNgxMask()
  ],
  templateUrl: './manager-home.component.html',
  styleUrl: './manager-home.component.css'
})
export class ManagerHomeComponent {

    solicitacoes = [
      {cpf: '12345678910', nome: "Tiago Salles", salario: 1500},
      {cpf: '12345678910', nome: "Tiago Salles", salario: 1500},
      {cpf: '12345678910', nome: "Tiago Salles", salario: 1500}
    ]


    //TODO: Definir função para reprovar ou aprovar a conta
    responseManager(id: String){
        if (id === "aproveButton"){
          alert("Cliente aprovado!");
        }else{
          alert("Cliente reprovado!");
        }
    }
}
