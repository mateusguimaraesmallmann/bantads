import { Routes } from '@angular/router';
import { ClientHomeComponent } from './features/client-home/client-home.component';
import { DepositComponent } from './features/deposit/deposit.component';
import { UpdateProfileComponent } from './features/update-profile/update-profile.component';
import { LoginComponent } from './features/login/login.component';
import { RegisterComponent } from './features/register/register.component';
import { ManagerHomeComponent } from './features/manager-home/manager-home.component';
import {TransactionHistoryComponent} from './features/transaction-history/transaction-history.component';
import {AdministratorHomeComponent} from './features/administrator/administrator-home/administrator-home.component';
import {ListManagerComponent} from './features/administrator/list-manager/list-manager.component';

export const routes: Routes = [

  { path: 'login', component: LoginComponent },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'register', component: RegisterComponent },
  { path: 'client-home', component: ClientHomeComponent },
  { path: 'deposit', component: DepositComponent },
  { path: 'update-profile', component: UpdateProfileComponent },
  { path: 'manager-home', component: ManagerHomeComponent},
  { path: 'transaction-history', component: TransactionHistoryComponent },
  { path: 'administrator-home', component: AdministratorHomeComponent },
  { path: 'list-manager', component: ListManagerComponent },

];
