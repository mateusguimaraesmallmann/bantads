import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
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
    password: ['', [Validators.required, Validators.minLength(6)]],
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

      this.authService.doLogin(this.login).subscribe({
        next: (response) => {


        },
        error: (err) => {
          this.hasError = true
        }
      })
      const payload = this.form.getRawValue();
      console.log('login payload', payload);
    }
  
  }

