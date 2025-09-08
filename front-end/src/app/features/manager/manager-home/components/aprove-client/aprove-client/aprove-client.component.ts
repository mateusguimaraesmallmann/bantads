import { Component } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal' 

@Component({
  selector: 'app-aprove-client',
  imports: [],
  templateUrl: './aprove-client.component.html',
  styleUrl: './aprove-client.component.css'
})
export class AproveClientComponent {

  constructor(public bsModalRef: BsModalRef) {}

  closeModal(){
    this.bsModalRef.hide();
  }
}
