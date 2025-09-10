import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../../core/components/header/header.component';
import { NgxMaskPipe, provideNgxMask } from 'ngx-mask';
import { NAVITEMS } from '../navItems';

@Component({
  selector: 'app-top-clients',
  imports: [CommonModule, HeaderComponent, NgxMaskPipe],
  providers: [
    provideNgxMask()
  ],
  templateUrl: './top-clients.component.html',
  styleUrl: './top-clients.component.css'
})
export class TopClientsComponent {
    navItems = NAVITEMS;

    topClients = [
      {cpf: "12345678910", nome: "Tara", cidade: "São Luís", estado: "MA", saldo:1000000},
      {cpf: "12345678910", nome: "Teste Nelson", cidade: "Curitiba", estado: "PR", saldo:1500000},
      {cpf: "12345678910", nome: "Clóvis dos Santos", cidade: "São Paulo", estado: "SP", saldo: 5000000000000000000000}
    ];

    ngOnInit(){
      this.topClients.sort((a,b) => b.saldo - a.saldo);
    }
}
