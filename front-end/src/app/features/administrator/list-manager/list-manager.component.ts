import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HeaderComponent } from '../../../core/components/header/header.component';
import { NgxMaskDirective, NgxMaskPipe, provideNgxMask } from 'ngx-mask';
import { ManagerService } from '../../../core/services/manager.service';
import { Manager } from '../../../core/models/manager';
import { NAVITEMS } from '../navItemsAdm';

// 👉 Tipagem dos controles do form
type ManagerForm = {
  nome: FormControl<string>;
  cpf: FormControl<string>;
  email: FormControl<string>;
  telefone: FormControl<string>;
  senha: FormControl<string>;
};

@Component({
  selector: 'app-list-manager',
  imports: [CommonModule, HeaderComponent, ReactiveFormsModule, NgxMaskDirective, NgxMaskPipe],
  providers: [
    provideNgxMask(),
  ],
  templateUrl: './list-manager.component.html',
  styleUrl: './list-manager.component.css'
})

export class ListManagerComponent implements OnInit{

  navItems = NAVITEMS;
  managers: Manager[] = [];

  constructor(private fb: FormBuilder, private managerService: ManagerService) {
    // use nonNullable para evitar null/undefined nos controles
    this.form = this.fb.nonNullable.group({
      nome: this.fb.nonNullable.control('', [Validators.required, Validators.minLength(3)]),
      cpf: this.fb.nonNullable.control('', [Validators.required]),
      email: this.fb.nonNullable.control('', [Validators.required, Validators.email]),
      telefone: this.fb.nonNullable.control('', [Validators.required]),
      senha: this.fb.nonNullable.control(''),
    });
  }

  ngOnInit(): void {
    this.managerService.listManagers().subscribe({
        next: (data) =>{
          this.managers = data;
          console.log(data)
        },
        error: (err) =>{
          console.error("Erro ao listar os gerentes.");
        }
    });
  }

  // 👉 FormGroup fortemente tipado
  form: FormGroup<ManagerForm>;
  modalOpen = false;
  editingIndex: number | null = null;

  //#region Abre o modal
  openInsert(): void {
    this.editingIndex = null;
    this.form.reset();
    this.form.get('senha')!.setValidators([Validators.required, Validators.minLength(4)]);
    this.form.get('senha')!.updateValueAndValidity();
    this.modalOpen = true;
  }

  //#region Editar Usuário
  openEdit(index: number): void {
    this.editingIndex = index;
    const m = this.managers[index];
    this.form.reset({
      nome: m.nome,
      cpf: m.cpf,
      email: m.email,
      telefone: m.telefone,
      senha: '',
    });
    this.form.get('senha')!.clearValidators();
    this.form.get('senha')!.updateValueAndValidity();
    this.modalOpen = true;
  }

  //#region Deleta o usuário
  delete(index: number): void {
    const copia = [...this.managers];
    copia.splice(index, 1);
    this.managers = copia; // reatribui para disparar CD
  }

  //#region Cria o usuário
  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = this.form.getRawValue();

    this.managerService.addManager(payload).subscribe({
      next: (response) =>{
        console.log("Gerente cadastrado com sucesso!");
      },
      error: (err) =>{
        console.error("Erro ao registrar gerente. Verificar se o usuário ainda está ativo ou se ");
      }
    })

    this.closeModal();
  }

  closeModal(): void {
    this.modalOpen = false;
  }

  get f(): ManagerForm { return this.form.controls; }

  trackByIndex = (i: number) => i;

}
