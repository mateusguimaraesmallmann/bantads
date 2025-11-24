import { Component, computed, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../../core/components/header/header.component';
import { NAVITEMS } from '../navItemsAdm';
import { AuthService } from '../../../core/services/authentication/auth.service';
import { forkJoin } from 'rxjs';
import { ManagerService } from '../../../core/services/manager.service';
import { UserService } from '../../../core/services/user.service';
import { AccountService } from '../../../core/services/account.service';
import { NgxMaskPipe, provideNgxMask } from 'ngx-mask';

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
  imports: [CommonModule, HeaderComponent, NgxMaskPipe],
  providers: [provideNgxMask()],
  templateUrl: './administrator-home.component.html',
  styleUrl: './administrator-home.component.css',
})
export class AdministratorHomeComponent implements OnInit {
  navItems = NAVITEMS;
  userName: string = "";

  listaBase = signal<LinhaDash[]>([]);
  filtroNome = signal<string>('');
  ordenarPor = signal<OrdenarPor>('somaPositivos');
  apenasNegativos = signal<boolean>(false);

  dashboardData = {
    totalGerentes: 0,
    totalClientes: 0,
    somaPositivosGlobal: 0,
    somaNegativosGlobal: 0,
    loading: true
  };

  constructor(
    private authService: AuthService,
    private managerService: ManagerService,
    private userService: UserService,
    private accountService: AccountService
  ) {
    const user = this.authService.getCurrentUser();
    this.userName = user ? user.name : 'Administrador';
  }

  ngOnInit(): void {
    this.carregarDashboard();
  }

  carregarDashboard() {
    this.dashboardData.loading = true;

    forkJoin({
      gerentes: this.managerService.listManagers(),
      clientes: this.userService.returnAllClients(),
      contas: this.accountService.returnAllAccounts()
    }).subscribe({
      next: ({ gerentes, clientes, contas }) => {
        this.dashboardData.totalGerentes = gerentes.length;
        this.dashboardData.totalClientes = clientes.length;

        this.dashboardData.somaPositivosGlobal = contas
          .filter(c => c.saldo >= 0)
          .reduce((acc, c) => acc + c.saldo, 0);

        this.dashboardData.somaNegativosGlobal = contas
          .filter(c => c.saldo < 0)
          .reduce((acc, c) => acc + c.saldo, 0);
        const dadosProcessados: LinhaDash[] = gerentes.map(gerente => {
          const contasDoGerente = contas.filter(c => c.idGerente === gerente.id);

          const somaPos = contasDoGerente
            .filter(c => c.saldo >= 0)
            .reduce((acc, c) => acc + c.saldo, 0);

          const somaNeg = contasDoGerente
            .filter(c => c.saldo < 0)
            .reduce((acc, c) => acc + c.saldo, 0);

          return {
            id: gerente.id,
            nome: gerente.nome,
            qtdClientes: contasDoGerente.length,
            somaPositivos: somaPos,
            somaNegativos: somaNeg
          };
        });

        this.listaBase.set(dadosProcessados);
        this.dashboardData.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar dados do dashboard:', err);
        this.dashboardData.loading = false;
      }
    });
  }
  linhasFiltradas = computed<LinhaDash[]>(() => {
    const termo = this.filtroNome().trim().toLowerCase();
    const onlyNeg = this.apenasNegativos();
    const ord = this.ordenarPor();
    const lista = this.listaBase();

    let result = lista.filter((l) => {
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
        default:
          return 0;
      }
    });

    return result;
  });

  asCurrency(v: number) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v);
  }

  onFiltroChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.filtroNome.set(target.value);
  }

  onOrdenarChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.ordenarPor.set(target.value as OrdenarPor);
  }

  onApenasNegativosChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.apenasNegativos.set(target.checked);
  }
}
