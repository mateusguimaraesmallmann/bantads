import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms'; 
import { FormBuilder, Validators } from '@angular/forms'
import { UserService } from '../../core/services/user.service';
import { UserUpdate } from '../../core/models/user-update.model';

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
    private userService: UserService) 
    {}
  
  updateUserForm!: FormGroup;
  updateStatus: 'idle' | 'loading' | 'success' | 'error' = 'idle';
  cpfUsuario:string = "00000000000";

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
  consultarUsuarioPorCpf(){
    this.updateStatus = 'loading'; 

    this.userService.consultarUsuario(this.cpfUsuario)
      .subscribe({
        next: (response) => {
          console.log('Dados recebidos com sucesso:', response);
          this.handleSuccess(response);
        },
        error: (err) => {
          console.error('Erro ao consultar usuário:', err);
          this.handleError(err);
        }
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
      cpf:this.cpfUsuario,
      email:formValues.email,
      salario:formValues.salario,
      endereco:formValues.endereco,
      CEP:formValues.CEP,
      cidade:formValues.cidade,
      estado:formValues.estado
    };

    this.updateStatus = 'loading';

    this.userService.atualizarUsuario(userUpdate, userUpdate.cpf).subscribe({
      next: (response) => this.handleSuccess(response),
      error: (err) => this.handleError(err)
    });
  }

  handleSuccess(response: UserUpdate): void {
    this.updateStatus = 'success';
    //this.testUser = response; 
  }

  handleError(error: any): void {
    this.updateStatus = 'error';
  }
}
