import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../../../core/components/header/header.component';
import { ClientDetails } from '../../../core/models/client-details.model';
import { NAVITEMS } from '../navItemsAdm';
import { AdministratorService } from '../../../core/services/administrator.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-administrator-client-list',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent],
  templateUrl: './administrator-client-list.component.html',
  styleUrls: ['./administrator-client-list.component.css']
})
export class AdministratorListaClientesComponent implements OnInit {
  navItems = NAVITEMS;
  loading:boolean = false;
  listaTodosClientes: ClientDetails[] = [];
  public listaClientesFiltrados: ClientDetails[] = [];
  public termoBusca: string = '';

  constructor(private adminService : AdministratorService) { }

  ngOnInit(): void {
    this.carregarClientes();
    this.ordenarEFiltrarClientes();
  }

  private carregarClientes(): void {

    const dadosDaApi: ClientDetails[] = [
      {
        id:1,
        cpf: "12912861012",
        nome: "Catharyna",
        email: "cli1@bantads.com.br",
        telefone: "(41) 9 9999-8989",
        endereco: "Rua X, nr 10",
        cidade: "Curitiba",
        estado: "PR",
        conta: "1291",
        saldo: 800,
        limite: 5000
      },
      {
        id:2,
        cpf: "09506382000",
        nome: "Cleuddônio",
        email: "cli2@bantads.com.br",
        telefone: "(11) 9 8888-7777",
        endereco: "Av Y, nr 20",
        cidade: "São Paulo",
        estado: "SP",
        conta: "2345",
        saldo: 25750.00,
        limite: 10000.00
      },
      {
        id:3,
        cpf: "85733854057",
        nome: "Catianna",
        email: "cli3@bantads.com.br",
        telefone: "(21) 9 7777-6666",
        endereco: "Praça Z, nr 30",
        cidade: "Rio de Janeiro",
        estado: "RJ",
        conta: "6789",
        saldo: -2800.75,
        limite: 1500.00
      }
    ];
    this.listaTodosClientes = dadosDaApi;
    // this.loading=true;
    //this.listaTodosClientes=[];
    // const subscription= this.adminService.listarTodosOsClientesAdministrator().subscribe({
    //   next: (response) => this.processarSucesso(() => this.listarTodosClientes(response, subscription)),
    //   error: (err) => this.processarErro(err, subscription)
    // });
  }

  public listarTodosClientes(clientes : ClientDetails[], subscription:Subscription){
    if(clientes != null && clientes.length > 0){
      this.listaTodosClientes = clientes;
    }
    this.loading = false;
    subscription.unsubscribe()
  }

  public ordenarEFiltrarClientes(): void {
    let clientesOrdenados = [...this.listaTodosClientes].sort((a, b) => a.nome.localeCompare(b.nome));

    if (this.termoBusca) {
      const termoBuscaMinusculo = this.termoBusca.toLowerCase();
      this.listaClientesFiltrados = clientesOrdenados.filter(cliente =>
        cliente.nome.toLowerCase().includes(termoBuscaMinusculo) ||
        cliente.cpf.replace(/[.-]/g, '').includes(termoBuscaMinusculo.replace(/[.-]/g, ''))
      );
    } else {
      this.listaClientesFiltrados = clientesOrdenados;
    }
  }

  processarSucesso(callback: () => void) {
    console.log("deu boa");
    callback();
  }

  processarErro(error:any, subscription:Subscription){
    console.log("deu ruim" + error);
    subscription.unsubscribe();
  }
}
