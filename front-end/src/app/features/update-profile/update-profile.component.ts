import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms'; 
import { FormBuilder, Validators } from '@angular/forms'
import { UserUpdate } from '../../models/user-update.model';
import { UserService } from '../../services/user.service';

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

  constructor(   
    private fb: FormBuilder, 
    private userService: UserService) {}
  
  updateUserForm!: FormGroup;
  updateStatus: 'idle' | 'loading' | 'success' | 'error' = 'idle';

  testUser: UserUpdate = {
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
    if (this.updateUserForm.invalid) {
      alert('Formulário inválido!');
      return; 
    }
     this.updateStatus = 'loading';
    const formValues = this.updateUserForm.value;

    const userUpdate: UserUpdate = {
      nome:formValues.nome,
      email:formValues.email,
      salario:formValues.salario,
      endereco:formValues.endereco,
      CEP:formValues.CEP,
      cidade:formValues.cidade,
      estado:formValues.estado
    };

    this.updateStatus = 'loading';
    this.userService.updateUser(userUpdate).subscribe({
      next: (response) => this.handleSuccess(response),
      error: (err) => this.handleError(err)
    });
  }

  handleSuccess(response: UserUpdate): void {
    this.updateStatus = 'success';
    this.testUser = response; 
  }

  handleError(error: any): void {
    this.updateStatus = 'error';
  }
}
