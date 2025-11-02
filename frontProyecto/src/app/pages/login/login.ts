import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss'],
})
export class LoginComponent {
  loading = false;
  errorMsg = '';
  form!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private auth: AuthService
  ) {
    this.form = this.fb.group({
      // deja Validators.email si quieres; para pruebas puedes quitarlo
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
    console.log('[LOGIN] Component ready');
  }

  get f() { return this.form.controls; }

  onSubmit() {
    console.log('[LOGIN] submit pressed');
    this.errorMsg = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      console.log('[LOGIN] form INVALID', {
        value: this.form.value,
        emailErrors: this.form.get('email')?.errors,
        passwordErrors: this.form.get('password')?.errors
      });
      this.errorMsg = 'Por favor corrige los campos.';
      return;
    }

    this.loading = true;
    const { email, password } = this.form.getRawValue();
    console.log('[LOGIN] calling API…', { email });

    this.auth.login({ email: email!, password: password! }).subscribe({
      next: () => {
        this.loading = false;
        console.log('[LOGIN] success');
        const role = (localStorage.getItem('role') || '').toUpperCase();
        console.log('[LOGIN] role from storage =', role);
        if (role === 'ADMIN') this.router.navigate(['/inicio-admin']);
        else this.router.navigate(['/inicio-usuario']);
      },
      error: (err: any) => {
        this.loading = false;
        console.error('[LOGIN] error', err);
        this.errorMsg =
          err?.error?.message ||
          (typeof err?.error === 'string' ? err.error : '') ||
          'No se pudo iniciar sesión. Verifica tus credenciales.';
      }
    });
  }
}
