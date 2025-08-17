import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms'; 
import { FormBuilder, Validators } from '@angular/forms'

@Component({
  selector: 'app-update-profile',
  imports: [
    CommonModule,
    FormsModule,         
    ReactiveFormsModule, 
  ],
  standalone: true,
  templateUrl: './update-profile.component.html',
  styleUrl: './update-profile.component.css'
})

export class UpdateProfileComponent implements OnInit{

  constructor(private fb: FormBuilder) {}
  
  updateUserForm!: FormGroup;

  testUser = {
    nome: "Crysthôncio",
    email: "crys@bantads.com.br",
    salario: 1506.88,
    endereco: "Rua das Palmeiras, 500",
    CEP: "80000000",
    cidade: "Curitiba",
    estado: "PR"
  };

  ngOnInit(): void {
  this.updateUserForm = this.fb.group({
    nome: [this.testUser.nome, [Validators.required]], 
    email: [this.testUser.email, [Validators.required, Validators.email]],
    salario: [this.testUser.salario, [Validators.required]],
    endereco: [this.testUser.endereco],
    CEP: [this.testUser.CEP],
    cidade: [this.testUser.cidade],
    estado: [this.testUser.estado]
    });
  }

  updateUser(): void {
    alert('Dados Atualizados');
  }  
}
