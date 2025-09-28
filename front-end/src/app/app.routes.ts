import { Routes } from '@angular/router';
import { ClientHomeComponent } from './features/client/client-home/client-home.component';
import { DepositComponent } from './features/client/deposit/deposit.component';
import { WithdrawComponent } from './features/client/withdraw/withdraw.component';
import { UpdateProfileComponent } from './features/client/client-update-profile/client-update-profile.component';
import { LoginComponent } from './features/login/login.component';
import { RegisterComponent } from './features/register/register.component';
import { ManagerHomeComponent } from './features/manager/manager-home/manager-home.component';
import {TransactionHistoryComponent} from './features/client/transaction-history/transaction-history.component';
import {AdministratorHomeComponent} from './features/administrator/administrator-home/administrator-home.component';
import {ListManagerComponent} from './features/administrator/list-manager/list-manager.component';
import { TransferComponent } from './features/client/transfer/transfer.component';
import {ManagerListaClientesComponent} from './features/manager/manager-client-list/manager-client-list.component';
import { TopClientsComponent } from './features/manager/top-clients/top-clients.component';
import { ClientSearchComponent } from './features/manager/client-search/client-search.component';
import { AdministratorListaClientesComponent} from './features/administrator/administrator-client-list/administrator-client-list.component';
import { AuthGuard } from './core/services/authentication/auth.guard';

export const routes: Routes = [

  { path: 'login', component: LoginComponent },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'register', component: RegisterComponent },

  { path: 'client-home', component: ClientHomeComponent, canActivate: [AuthGuard], data: { roles: ['CLIENT'] }},
  { path: 'deposit', component: DepositComponent, canActivate: [AuthGuard], data: { roles: ['CLIENT'] } },
  { path: 'withdraw', component: WithdrawComponent, canActivate: [AuthGuard], data: { roles: ['CLIENT'] } },
  { path: 'transfer', component: TransferComponent, canActivate: [AuthGuard], data: { roles: ['CLIENT'] } },
  { path: 'update-profile', component: UpdateProfileComponent, canActivate: [AuthGuard], data: { roles: ['CLIENT'] } },
  { path: 'manager-home', component: ManagerHomeComponent, canActivate: [AuthGuard], data: { roles: ['MANAGER'] } },
  { path: 'transaction-history', component: TransactionHistoryComponent, canActivate: [AuthGuard], data: { roles: ['CLIENT'] } },
  { path: 'administrator-home', component: AdministratorHomeComponent/*, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } */},
  { path: 'list-manager', component: ListManagerComponent/*, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } */},
  { path: 'manager-client-list', component: ManagerListaClientesComponent, canActivate: [AuthGuard], data: { roles: ['MANAGER'] } },
  { path: 'top-clients', component: TopClientsComponent, canActivate: [AuthGuard], data: { roles: ['MANAGER'] } },
  { path: 'search-client', component: ClientSearchComponent, canActivate: [AuthGuard], data: { roles: ['MANAGER'] } },
  { path: 'administrator-client-list', component: AdministratorListaClientesComponent/*, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } */}
];
