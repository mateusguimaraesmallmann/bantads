import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../../core/components/header/header.component';
import { NAVITEMS } from '../navItems';

@Component({
  selector: 'app-top-clients',
  imports: [CommonModule, HeaderComponent],
  templateUrl: './top-clients.component.html',
  styleUrl: './top-clients.component.css'
})
export class TopClientsComponent {
    navItems = NAVITEMS;
}
