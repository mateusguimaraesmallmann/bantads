import { Component, Input } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { User } from '../../models/user.model';
import { AuthService } from '../../services/authentication/auth.service';

@Component({
  selector: 'app-header',
  imports: [NgbModule, CommonModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
    currentUser: User | null = null;

    constructor(private authService: AuthService){
      this.currentUser = this.authService.getCurrentUser();
    }

    onLogout() {
      this.authService.logout();
    }

    @Input() navItems: any[] = [];
}