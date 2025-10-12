import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, Validators } from '@angular/forms'
import { UserService } from '../../../core/services/user.service';
import { ClientUpdate } from '../../../core/models/client-update.model';
import { catchError, debounceTime, distinctUntilChanged, filter, of, Subject, Subscription, switchMap, takeUntil, tap } from 'rxjs';
import { Router } from '@angular/router';
import { ViacepService } from '../../../core/services/viacep.service';
import { NgxMaskDirective } from 'ngx-mask';

@Component({
  selector: 'app-update-profile',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgxMaskDirective
  ],
  standalone: true,
  templateUrl: './client-update-profile.component.html',
  styleUrl: './client-update-profile.component.css'
})

export class UpdateProfileComponent implements OnInit{

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router,
    private viaCep: ViacepService)
    {}

  clientUpdateForm!: FormGroup;
  status: 'idle' | 'loading' | 'success' | 'error' = 'idle';
  cpfUsuario:string = "00000000000";
  private destroy$ = new Subject<void>();
  loadingCep = false;
  cepNotFound = false;

  testUser = {
    nome: "Paulo Silva",
    email: "paulo.silva@example.com",
    salario: 1550.75,
    cep:"80000000",
    logradouro: "Rua das Palmeiras",
    numero: "500",
    complemento: "Apto 301",
    bairro: "Centro",
    cidade: "Curitiba",
    estado: "PR"
  };

  ngOnInit(): void {
    this.clientUpdateForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      salario: [0, [Validators.required, Validators.min(0)]],
      cep: ['', [Validators.required, Validators.pattern(/^\d{5}-?\d{3}$/)]],
      logradouro: [''],
      numero: ['', [Validators.required]],
      complemento: [''],
      bairro: [''],
      cidade: [''],
      estado: ['']
    });
    //this.consultarUsuarioPorCpf();
    this.popularFormulario();
    this.setupCepLookup();
  }

  private setupCepLookup() {
    this.clientUpdateForm?.get('cep')?.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      filter((v): v is string => !!v && v.replace(/\D/g, '').length === 8),
      tap(() => { this.loadingCep = true; this.cepNotFound = false; }),
      switchMap(cep => this.viaCep.getByCep(cep).pipe(
        catchError(() => { this.cepNotFound = true; return of(null); })
      ))
    ).subscribe(res => {
      this.loadingCep = false;

      if (!res || (res as any)?.erro) {
        this.cepNotFound = true;
        return;
      }

      this.clientUpdateForm.patchValue({
        logradouro: res!.logradouro || '',
        bairro: res!.bairro || '',
        cidade: res!.localidade || '',
        estado: res!.uf || ''
      });
    });
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
    this.clientUpdateForm.patchValue ({
      nome: this.testUser.nome,
      email: this.testUser.email,
      salario: this.testUser.salario,
      cep: this.testUser.cep,
      logradouro: this.testUser.logradouro,
      numero: this.testUser.numero,
      complemento: this.testUser.complemento,
      bairro: this.testUser.bairro,
      cidade: this.testUser.cidade,
      estado: this.testUser.estado
      });
  }

  atualizarUsuario(): void {
    if (this.clientUpdateForm.invalid) {
      this.clientUpdateForm.markAllAsTouched();
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
      cep:formValues.cep,
      logradouro:formValues.logradouro,
      numero:formValues.numero,
      complemento:formValues.complemento,
      bairro:formValues.bairro,
      cidade:formValues.cidade,
      estado:formValues.estado
    };

    this.status = 'loading';

    this.userService.atualizarUsuario(userUpdate, userUpdate.cpf).subscribe({
      next: (response) => this.processarSucesso(),
      error: (err) => this.processarErro(err)
    });
  }

  voltar(): void {
    this.router.navigate(['/client-home']);
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
