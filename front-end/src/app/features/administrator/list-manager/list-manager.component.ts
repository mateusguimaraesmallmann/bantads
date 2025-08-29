import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HeaderComponent } from '../../../core/components/header/header.component';

interface Manager {
  nome: string;
  cpf: string;
  email: string;
  telefone: string;
  senha?: string;
}

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
  imports: [CommonModule, HeaderComponent, ReactiveFormsModule],
  templateUrl: './list-manager.component.html',
  styleUrl: './list-manager.component.css'
})

export class ListManagerComponent {

  // Lista mockada (tipada corretamente)
  managers: Manager[] = [
    { nome: 'Ana Souza', cpf: '111.222.333-44', email: 'ana@banco.com', telefone: '(41) 99999-0001' },
    { nome: 'Bruno Lima', cpf: '555.666.777-88', email: 'bruno@banco.com', telefone: '(41) 99999-0002' },
    { nome: 'Carla Dias', cpf: '123.456.789-00', email: 'carla@banco.com', telefone: '(41) 99999-0003' },
  ];

  // 👉 FormGroup fortemente tipado
  form: FormGroup<ManagerForm>;
  modalOpen = false;
  editingIndex: number | null = null;

  constructor(private fb: FormBuilder) {
    // use nonNullable para evitar null/undefined nos controles
    this.form = this.fb.nonNullable.group({
      nome: this.fb.nonNullable.control('', [Validators.required, Validators.minLength(3)]),
      cpf: this.fb.nonNullable.control('', [Validators.required]),
      email: this.fb.nonNullable.control('', [Validators.required, Validators.email]),
      telefone: this.fb.nonNullable.control('', [Validators.required]),
      senha: this.fb.nonNullable.control(''),
    });
  }

  openInsert(): void {
    this.editingIndex = null;
    this.form.reset();
    this.form.get('senha')!.setValidators([Validators.required, Validators.minLength(4)]);
    this.form.get('senha')!.updateValueAndValidity();
    this.modalOpen = true;
  }

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

  delete(index: number): void {
    const copia = [...this.managers];
    copia.splice(index, 1);
    this.managers = copia; // reatribui para disparar CD
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { nome, cpf, email, telefone } = this.form.value as Manager;

    if (this.editingIndex === null) {
      // inserção
      const novo: Manager = { nome, cpf, email, telefone };
      this.managers = [novo, ...this.managers];
    } else {
      // edição
      const copia = [...this.managers];
      copia[this.editingIndex] = { ...copia[this.editingIndex], nome, cpf, email, telefone };
      this.managers = copia;
    }

    this.closeModal();
  }

  closeModal(): void {
    this.modalOpen = false;
  }

  get f(): ManagerForm { return this.form.controls; }

  trackByIndex = (i: number) => i;

}