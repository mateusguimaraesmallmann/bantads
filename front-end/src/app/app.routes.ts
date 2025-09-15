import { Routes } from '@angular/router';
import { ClientHomeComponent } from './features/client-home/client-home.component';
import { DepositComponent } from './features/deposit/deposit.component';
import { WithdrawComponent } from './features/withdraw/withdraw.component';
import { UpdateProfileComponent } from './features/update-profile/update-profile.component';
import { LoginComponent } from './features/login/login.component';
import { RegisterComponent } from './features/register/register.component';
import { ManagerHomeComponent } from './features/manager/manager-home/manager-home.component';
import {TransactionHistoryComponent} from './features/transaction-history/transaction-history.component';
import {AdministratorHomeComponent} from './features/administrator/administrator-home/administrator-home.component';
import {ListManagerComponent} from './features/administrator/list-manager/list-manager.component';
import { TransferComponent } from './features/transfer/transfer.component';
import {ManagerListaClientesComponent} from './features/manager/manager-client-list/manager-client-list.component';
import { TopClientsComponent } from './features/manager/top-clients/top-clients.component';
import { ClientSearchComponent } from './features/manager/client-search/client-search.component';
import { AdministratorListaClientesComponent} from './features/administrator/administrator-client-list/administrator-client-list.component';

export const routes: Routes = [

  { path: 'login', component: LoginComponent },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'register', component: RegisterComponent },
  { path: 'client-home', component: ClientHomeComponent },
  { path: 'deposit', component: DepositComponent },
  { path: 'withdraw', component: WithdrawComponent },
  { path: 'transfer', component: TransferComponent },
  { path: 'update-profile', component: UpdateProfileComponent },
  { path: 'manager-home', component: ManagerHomeComponent},
  { path: 'transaction-history', component: TransactionHistoryComponent },
  { path: 'administrator-home', component: AdministratorHomeComponent },
  { path: 'list-manager', component: ListManagerComponent },
  {path: 'manager-client-list',component:ManagerListaClientesComponent},
  {path: 'top-clients', component:TopClientsComponent},
  {path:'search-client', component:ClientSearchComponent},
  {path: 'administrator-client-list',component:AdministratorListaClientesComponent},  
];
