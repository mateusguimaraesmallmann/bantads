import { Component } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-deny-client',
  imports: [],
  templateUrl: './deny-client.component.html',
  styleUrl: './deny-client.component.css'
})
export class DenyClientComponent {

  constructor(public bsModalRef: BsModalRef){}

  closeModal(){
    this.bsModalRef.hide();
  }
}
