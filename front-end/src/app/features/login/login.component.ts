import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/authentication/auth.service';
import { Login } from '../../core/models/login.model';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  year = new Date().getFullYear();
  private fb = inject(FormBuilder);
  showPassword = signal(false);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  login: Login = new Login();
  hasError = false;

  constructor(private authService: AuthService) {}

  get f() {
    return this.form.controls;
  }

  submit(): any {
  if (this.form.invalid) {
    this.form.markAllAsTouched();
    return;
  }

  const payload = this.form.getRawValue() as Login;
  console.log('login payload', payload);

  this.authService.doLogin(payload).subscribe({
    next: (user) => {
      if (user) {
        if (user.role === 'ADMIN') {
          window.location.href = '/administrator-home';
        } else if (user.role === 'MANAGER') {
          window.location.href = '/manager-home';
        } else if (user.role === 'CLIENT') {
          window.location.href = '/client-home';
        }
      } else {
        this.hasError = true;
        console.log("Usuário ou senha incorretos!")
      }
    },
    error: (err) => {
      this.hasError = true;
      console.error("Erro:", err);
    }
  });
  }
}
