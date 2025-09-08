import { Component, computed, inject, signal } from '@angular/core';
import { ManagerService } from '../../../core/services/manager.service';

type LinhaDash = {
  id: number;
  nome: string;
  qtdClientes: number;
  somaPositivos: number;
  somaNegativos: number;
};

type OrdenarPor = 'somaPositivos' | 'somaNegativos' | 'qtdClientes' | 'nome';

@Component({
  selector: 'app-administrator-home',
  standalone: true,
  imports: [],
  templateUrl: './administrator-home.component.html',
  styleUrl: './administrator-home.component.css',
})
export class AdministratorHomeComponent {
  private readonly service = inject(ManagerService);

  filtroNome = signal<string>('');
  ordenarPor = signal<OrdenarPor>('somaPositivos');
  apenasNegativos = signal<boolean>(false);

  private readonly base = computed<LinhaDash[]>(() => {
    const gerentes = this.service.gerentes();
    const linhas = gerentes.map((g) => {
      const somaPositivos = g.clientes
        .filter((c) => c.saldo >= 0)
        .reduce((acc, c) => acc + c.saldo, 0);
      const somaNegativos = g.clientes
        .filter((c) => c.saldo < 0)
        .reduce((acc, c) => acc + c.saldo, 0);
      return {
        id: g.id,
        nome: g.nome,
        qtdClientes: g.clientes.length,
        somaPositivos,
        somaNegativos,
      };
    });
    return linhas;
  });

  linhasFiltradas = computed<LinhaDash[]>(() => {
    const termo = this.filtroNome().trim().toLowerCase();
    const onlyNeg = this.apenasNegativos();
    const ord = this.ordenarPor();

    let result = this.base().filter((l) => {
      const byNome = !termo || l.nome.toLowerCase().includes(termo);
      const byNeg = !onlyNeg || l.somaNegativos < 0;
      return byNome && byNeg;
    });

    result = [...result].sort((a, b) => {
      switch (ord) {
        case 'somaPositivos':
          return b.somaPositivos - a.somaPositivos;
        case 'somaNegativos':
          return a.somaNegativos - b.somaNegativos;
        case 'qtdClientes':
          return b.qtdClientes - a.qtdClientes;
        case 'nome':
          return a.nome.localeCompare(b.nome);
      }
    });

    return result;
  });

  totais = computed(() => {
    const linhas = this.linhasFiltradas();
    return {
      qtdClientes: linhas.reduce((acc, l) => acc + l.qtdClientes, 0),
      somaPositivos: linhas.reduce((acc, l) => acc + l.somaPositivos, 0),
      somaNegativos: linhas.reduce((acc, l) => acc + l.somaNegativos, 0),
    };
  });

  asCurrency(v: number) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v);
  }
  
}