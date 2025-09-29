
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router'; 
import { HeaderComponent } from '../../../core/components/header/header.component';
import { NAVITEMS } from '../navItems';
import { ClientDetails } from '../../../core/models/client-details.model';

@Component({
  selector: 'app-manager-client-details',
  standalone: true,
  imports: [CommonModule, HeaderComponent, RouterModule],
  templateUrl: './manager-client-details.component.html',
  styleUrl: './manager-client-details.component.css'
})
export class ManagerClientDetailsComponent implements OnInit {
  navItems = NAVITEMS;
  cliente: ClientDetails | undefined;
  isLoading: boolean = true;

  private todosClientes: ClientDetails[] = [
    { cpf: "12912861012", nome: "Catharyna Pires", email: "cli1@bantads.com.br", telefone: "(41) 9 9999-8989", endereco: "Rua das Flores, 123", cidade: "Curitiba", estado: "PR", conta: "1291", saldo: -800, limite: 5000 },
    { cpf: "09506382000", nome: "Cleuddônio Silva", email: "cli2@bantads.com.br", telefone: "(11) 9 8888-7777", endereco: "Avenida Paulista, 1500", cidade: "São Paulo", estado: "SP", conta: "2345", saldo: 25750.00, limite: 10000.00 },
    { cpf: "85733854057", nome: "Catianna Souza", email: "cli3@bantads.com.br", telefone: "(21) 9 7777-6666", endereco: "Praça da Apoteose, 50", cidade: "Rio de Janeiro", estado: "RJ", conta: "6789", saldo: 2800.75, limite: 1500.00 }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router 
  ) { }

  ngOnInit(): void {
    const cpf = this.route.snapshot.paramMap.get('cpf');
    
    if (cpf) {
      this.cliente = this.todosClientes.find(c => c.cpf === cpf);
      this.isLoading = false;
    } else {
      this.isLoading = false;
      console.error('CPF não encontrado na URL');
    }
  }

  voltarParaLista(): void {
    this.router.navigate(['/manager-client-list']); 
  }
}