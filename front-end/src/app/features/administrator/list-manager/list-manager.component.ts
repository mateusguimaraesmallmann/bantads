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
    const managerToDelete = this.managers.at(index);
    if (managerToDelete != null){
      this.managerService.disableManager(managerToDelete.cpf).subscribe({
        next: (data) => {
          copia.splice(index, 1);
          this.managers = copia; // reatribui para disparar CD
          console.log(data);
        },
        error: (err) => {
          console.error("Erro: ", err)
        }
      })
    }

  }

  confirmDeleteOpen = false;
  managerToDelete: Manager | null = null;
  deleteIndex: number | null = null;

  openConfirmDelete(index: number): void {
    this.managerToDelete = this.managers[index];
    this.deleteIndex = index;
    this.confirmDeleteOpen = true;
  }

  confirmDelete(): void {
    if (this.deleteIndex == null || this.managerToDelete == null) return;

    const index = this.deleteIndex;
    const managerToDelete = this.managerToDelete;

    this.managerService.disableManager(managerToDelete.cpf).subscribe({
      next: () => {
        this.managers.splice(index, 1);
        this.managers = [...this.managers];
        this.closeConfirmDelete();
      },
      error: (err) => {
        console.error('Erro ao excluir gerente:', err);
        this.closeConfirmDelete();
      }
    });
  }

  closeConfirmDelete(): void {
    this.confirmDeleteOpen = false;
    this.managerToDelete = null;
    this.deleteIndex = null;
  }

  //#region Cria/Atualiza
  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = this.form.getRawValue();

    //Flag para controlar se vai atualizar ou criar o gerente
    const findUser = this.managers.find(manager => manager.cpf === payload.cpf)

    //Condição de controle da criação e edição de gerentes
    if (!findUser){
      this.managerService.addManager(payload).subscribe({
        next: (response) =>{
          console.log("Gerente cadastrado com sucesso!");
          const newManager: Manager = {
            id: 0,
            nome: payload.nome,
            email: payload.email,
            cpf: payload.cpf,
            telefone: payload.telefone
          }
          this.managers.push(newManager)
        },
        error: (err) =>{
          console.error("Erro ao registrar gerente. Verificar se o usuário ainda está ativo.");
          console.log(err);
        }
      })
    } else{
      this.managerService.updateManager(payload).subscribe({
        next: (response) => {
          console.log("Gerente atualizado com sucesso!");
          const newManager: Manager = {
            id: 0,
            nome: payload.nome,
            email: payload.email,
            cpf: payload.cpf,
            telefone: payload.telefone
          }
          this.managers[this.managers.indexOf(findUser)] = newManager
        },
        error: (err) => {
          console.error("Erro: ", err);
        }
      })
    }

    this.closeModal();
  }

  closeModal(): void {
    this.modalOpen = false;
  }

  get f(): ManagerForm { return this.form.controls; }

  trackByIndex = (i: number) => i;

}
