import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import{ ClientDetailsCpf } from '../../../core/models/client-details.model';

@Component({
  selector: 'app-client-search',
  imports: [CommonModule, FormsModule],
  templateUrl: './client-search.component.html',
  styleUrl: './client-search.component.css'
})
export class ClientSearchComponent {
  public cpfPesquisa: string = '';
  public clienteEncontrado: ClientDetailsCpf | null = null;
  public mensagem: string = '';
  public buscaRealizada: boolean = false;
  private gerentes = {
    'Geniéve': { cpf: '98574307084', email: 'ger1@bantads.com.br' },
    'Godophredo': { cpf: '12345678901', email: 'ger2@bantads.com.br' },
    'Gyândula': { cpf: '98765432109', email: 'ger3@bantads.com.br' }
  };

  private mock_clientes: ClientDetailsCpf[] = [
    {
      cpf: '12912861012',
      nome: 'Catharyna',
      email: 'cli1@bantads.com.br',
      telefone: '(41) 99999-8989',
      endereco: 'Rua das Flores, 123',
      cidade: 'Curitiba',
      estado: 'PR',
      salario: 10000.00,
      conta: '1291',
      saldo: 800.00,
      limite: 5000.00,
      gerente: this.gerentes['Geniéve'].cpf,
      gerente_nome: 'Geniéve',
      gerente_email: this.gerentes['Geniéve'].email,
    },
    {
      cpf: '09506382000',
      nome: 'Cleuddônio',
      email: 'cli2@bantads.com.br',
      telefone: '(11) 98888-7777',
      endereco: 'Avenida Paulista, 1000',
      cidade: 'São Paulo',
      estado: 'SP',
      salario: 20000.00,
      conta: '0950',
      saldo: -10000.00,
      limite: 10000.00,
      gerente: this.gerentes['Godophredo'].cpf,
      gerente_nome: 'Godophredo',
      gerente_email: this.gerentes['Godophredo'].email,
    },
    {
      cpf: '85733854057',
      nome: 'Catianna',
      email: 'cli3@bantads.com.br',
      telefone: '(21) 97777-6666',
      endereco: 'Rua de Copacabana, 500',
      cidade: 'Rio de Janeiro',
      estado: 'RJ',
      salario: 3000.00,
      conta: '8573',
      saldo: -1000.00,
      limite: 1500.00,
      gerente: this.gerentes['Gyândula'].cpf,
      gerente_nome: 'Gyândula',
      gerente_email: this.gerentes['Gyândula'].email,
    },
    {
      cpf: '58872160006',
      nome: 'Cutardo',
      email: 'cli4@bantads.com.br',
      telefone: '(31) 96666-5555',
      endereco: 'Praça da Liberdade, 10',
      cidade: 'Belo Horizonte',
      estado: 'MG',
      salario: 500.00,
      conta: '5887',
      saldo: 150000.00,
      limite: 0.00,
      gerente: this.gerentes['Geniéve'].cpf,
      gerente_nome: 'Geniéve',
      gerente_email: this.gerentes['Geniéve'].email,
    },
    {
      cpf: '76179646090',
      nome: 'Coândrya',
      email: 'cli5@bantads.com.br',
      telefone: '(51) 95555-4444',
      endereco: 'Avenida Ipiranga, 3000',
      cidade: 'Porto Alegre',
      estado: 'RS',
      salario: 1500.00,
      conta: '7617',
      saldo: 1500.00,
      limite: 0.00,
      gerente: this.gerentes['Godophredo'].cpf,
      gerente_nome: 'Godophredo',
      gerente_email: this.gerentes['Godophredo'].email,
    },
  ];  
  constructor() {}
  
  public buscarCliente(): void {
    this.clienteEncontrado = null;
    this.mensagem = '';
    this.buscaRealizada = true;

    if (!this.cpfPesquisa) {
      this.mensagem = 'Por favor, digite um CPF para realizar a busca.';
      return;
    }
    const cpfFormatado = this.cpfPesquisa.replace(/[.-]/g, '');
    const resultado = this.mock_clientes.find(
      (cliente) => cliente.cpf === cpfFormatado
    );

    if (resultado) {
      this.clienteEncontrado = resultado;
    } else {
      this.mensagem = 'Cliente não encontrado. Verifique o CPF digitado.';
    }
  }
}
