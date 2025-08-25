import { Component, computed, inject } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { ManagerService } from '../../core/services/manager.service';

type LinhaDash = {
  id: number;
  nome: string;
  qtdClientes: number;
  somaPositivos: number;
  somaNegativos: number;
};

@Component({
  selector: 'app-administrator-home',
  imports: [],
  templateUrl: './administrator-home.component.html',
  styleUrl: './administrator-home.component.css'
})
export class AdministratorHomeComponent {
  private readonly service = inject(ManagerService);

  linhas = computed<LinhaDash[]>(() => {
    const base = this.service.gerentes();
    const linhas = base.map(g => {
      const somaPositivos = g.clientes
        .filter(c => c.saldo >= 0)
        .reduce((acc, c) => acc + c.saldo, 0);
      const somaNegativos = g.clientes
        .filter(c => c.saldo < 0)
        .reduce((acc, c) => acc + c.saldo, 0);

      return {
        id: g.id,
        nome: g.nome,
        qtdClientes: g.clientes.length,
        somaPositivos,
        somaNegativos,
      };
    });

    return linhas.sort((a, b) => b.somaPositivos - a.somaPositivos);
  });

  asCurrency(v: number) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v);
  }

}