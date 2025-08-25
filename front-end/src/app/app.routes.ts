import { Routes } from '@angular/router';
import { ClientHomeComponent } from './features/client-home/client-home.component';
import { UpdateProfileComponent } from './features/update-profile/update-profile.component';
import { LoginComponent } from './features/login/login.component';
import { RegisterComponent } from './features/register/register.component';
import {TransactionHistoryComponent} from './features/transaction-history/transaction-history.component';

export const routes: Routes = [
  
  { path: 'login', component: LoginComponent },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'register', component: RegisterComponent },
  { path: 'client-home', component: ClientHomeComponent },
  { path: 'update-profile', component: UpdateProfileComponent },
  { path: 'transaction-history', component: TransactionHistoryComponent },

];
