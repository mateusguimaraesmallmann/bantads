import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ViacepService } from '../../../app/core/services/viacep.service';
import { RegisterService } from '../../core/services/authentication/register.service';
import { debounceTime, distinctUntilChanged, filter, switchMap, tap, catchError, of } from 'rxjs';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';
import { RouterLink, Router } from '@angular/router';
import { NewUser } from '../../core/models/new-client.model';

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule,  NgxMaskDirective, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  year = new Date().getFullYear();
  loadingCep = false;
  cepNotFound = false;

  constructor(private fb: FormBuilder, private viaCep: ViacepService, private registerService: RegisterService, private router:Router) {
    this.form.get('cep')!.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      tap(() => { this.cepNotFound = false; }),
      mapVal => mapVal
    );
    this.setupCepLookup();
  }

  form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    cpf: ['', [Validators.required]],
    salario: [null as number | null, [Validators.required, Validators.min(0)]],
    cep: ['', [Validators.required, Validators.pattern(/^\d{5}-?\d{3}$/)]],

    logradouro: [''],
    bairro: [''],
    numero: ['', [Validators.required]],
    complemento: [''],
    cidade: [''],
    estado: [''],
  });

  get f() { return this.form.controls; }

  private setupCepLookup() {
    this.form.get('cep')!.valueChanges.pipe(
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

      this.form.patchValue({
        logradouro: res!.logradouro || '',
        bairro: res!.bairro || '',
        cidade: res!.localidade || '',
        estado: res!.uf || ''
      });
    });
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = this.form.getRawValue();

    const salarioStr = payload.salario?.toString() ?? '';
    const onlyNumbers = salarioStr.replace(/[^\d]/g, '');
    const salario = Number(onlyNumbers) / 100;

    const newUserPayload: NewUser = {
      ...payload,
      salario: salario,
      status: "PENDENT",
      role: "CLIENT"
    }

    this.registerService.registerClient(newUserPayload).subscribe({
      next: (response) =>{
        console.log('Cliente cadastrado com sucesso!', response);
        alert('Cadastro enviado para análise. Quando aprovado, sua senha será encaminhada no seu e-mail.');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Erro no cadastro: ', err);
        alert(err.message)
      }
    })

  }

}
