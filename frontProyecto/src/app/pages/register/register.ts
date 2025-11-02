import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, HttpClientModule],
  templateUrl: './register.html',
  styleUrls: ['./register.scss'],
})
export class RegisterComponent {
  form!: FormGroup;
  loading = false;
  private API = '/api';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    // Definición del formulario para crear la empresa
    this.form = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      nit: ['', [Validators.required]],
      correoContacto: ['', [Validators.required, Validators.email]],
      empresaPassword: ['', [Validators.required]],
    });
  }

  get c() { return this.form.controls; }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;

    const payloadEmpresa = {
      nombre: String(this.c['nombre'].value || '').trim(),
      nit: String(this.c['nit'].value || '').trim(),
      correoContacto: String(this.c['correoContacto'].value || '').trim(),
      password: String(this.c['empresaPassword'].value || '').trim(),
    };

    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

    this.http.post(`${this.API}/empresas`, payloadEmpresa, {
      headers,
      observe: 'response',
      responseType: 'json'
    }).subscribe({
      next: (res) => {
        this.loading = false;
        this.router.navigateByUrl('/inicio-admin');
      },
      error: (e) => {
        this.loading = false;
        const msg =
          e?.error?.message || e?.error?.error ||
          (typeof e?.error === 'string' && e.error) ||
          'No se pudo crear la empresa';
        alert(msg);
      }
    });
  }
}
