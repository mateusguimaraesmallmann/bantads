import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms'; 
import { FormBuilder, Validators } from '@angular/forms'
import { UserService } from '../../core/services/user.service';
import { ClientUpdate } from '../../core/models/client-update.model';
import { Subject, Subscription, takeUntil } from 'rxjs';

@Component({
  selector: 'app-update-profile',
  imports: [
    CommonModule,
    FormsModule,         
    ReactiveFormsModule, 
  ],
  standalone: true,
  templateUrl: './client-update-profile.component.html',
  styleUrl: './client-update-profile.component.css'
})

export class UpdateProfileComponent implements OnInit{

  constructor(   
    private fb: FormBuilder, 
    private userService: UserService) 
    {}
  
  clientUpdateForm!: FormGroup;
  status: 'idle' | 'loading' | 'success' | 'error' = 'idle';
  cpfUsuario:string = "00000000000";
  private destroy$ = new Subject<void>();
  
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
    this.clientUpdateForm = this.fb.group({
      nome: ['', [Validators.required]], 
      email: ['', [Validators.required, Validators.email]],
      salario: [0, [Validators.required]],
      endereco: [''],
      CEP: [''],
      cidade: [''],
      estado: ['']
    });
    //this.consultarUsuarioPorCpf();
    this.popularFormulario();
  }

  consultarUsuarioPorCpf(){
    this.status = 'loading'; 
    this.userService.consultarUsuario(this.cpfUsuario)
      .pipe(
        takeUntil(this.destroy$) 
      )
      .subscribe({
        next: (response) => {
          console.log('Dados recebidos com sucesso:', response);
          this.popularFormulario();
        },
        error: (err) => {
          console.error('Erro ao consultar usuário:', err);
          this.processarErro(err);
        }
      });
  }

  popularFormulario(//response: ClientUpdate
    ){
    this.clientUpdateForm = this.fb.group({
      nome: [this.testUser.nome, [Validators.required]], 
      email: [this.testUser.email, [Validators.required, Validators.email]],
      salario: [this.testUser.salario, [Validators.required]],
      endereco: [this.testUser.endereco],
      CEP: [this.testUser.CEP],
      cidade: [this.testUser.cidade],
      estado: [this.testUser.estado]
      });
  }

  atualizarUsuario(): void {
    if (this.clientUpdateForm.invalid) {
      alert('Formulário inválido!');
      return; 
    }
     this.status = 'loading';
    const formValues = this.clientUpdateForm.value;

    const userUpdate: ClientUpdate = {
      nome:formValues.nome,
      cpf:this.cpfUsuario,
      email:formValues.email,
      salario:formValues.salario,
      endereco:formValues.endereco,
      CEP:formValues.CEP,
      cidade:formValues.cidade,
      estado:formValues.estado
    };

    this.status = 'loading';

    this.userService.atualizarUsuario(userUpdate, userUpdate.cpf).subscribe({
      next: (response) => this.processarSucesso(),
      error: (err) => this.processarErro(err)
    });
  }

  processarSucesso(): void {
    this.status = 'success';
  }

  processarErro(error: any): void {
    this.status = 'error';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }  
}
