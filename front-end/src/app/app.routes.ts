import { Routes } from '@angular/router';
import { ClientHomeComponent } from './features/client-home/client-home.component';
import { UpdateProfileComponent } from './features/update-profile/update-profile.component';

export const routes: Routes = [
  { path: 'client-home', component: ClientHomeComponent },
  { path: 'update-profile', component: UpdateProfileComponent }
];
