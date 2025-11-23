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
import { ClienteCompleto } from '../../../core/models/account.model';
import { AuthService } from '../../../core/services/authentication/auth.service';

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
    private viaCep: ViacepService,
    private authService: AuthService)
    {}

  clientUpdateForm!: FormGroup;
  status: 'idle' | 'loading' | 'success' | 'error' = 'idle';
  cpfUsuario:string = "";
  private destroy$ = new Subject<void>();
  loadingCep = false;
  cepNotFound = false;
  user: any;

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
    let infos = this.authService.getCurrentUser();
    this.cpfUsuario = infos?.cpf ? infos?.cpf : "";
    this.consultarUsuarioPorCpf();
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
    this.userService.consultarClienteSaga(this.cpfUsuario)
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (response) => {
          this.popularFormulario(response);
        },
        error: (err) => {
          console.error('Erro ao consultar usuário:', err);
          this.processarErro(err);
        }
      });
  }

  popularFormulario(response: ClienteCompleto
    ){
    this.clientUpdateForm.patchValue ({
      nome: response.nome,
      email: response.email,
      salario: response.salario,
      //cep: response.cep,
      logradouro: response.cidade,
      // numero: response.numero,
      // complemento: response.complemento,
      // bairro: response.bairro,
      cidade: response.cidade,
      estado: response.estado
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
