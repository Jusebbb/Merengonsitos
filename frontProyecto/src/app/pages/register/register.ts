import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

type Rol = 'empleado' | 'administrador';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, HttpClientModule],
  templateUrl: './register.html',
  styleUrls: ['./register.scss'],
})
export class RegisterComponent {
  form!: FormGroup;

  private API = '/api';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.form = this.fb.group({
      role: ['empleado' as Rol, [Validators.required]],

      // ------- Campos de EMPLEADO -------
      name: ['', []],
      email: ['', []],
      password: ['', []],
      confirm: ['', []],
      empresaId: [null, []],

      // ------- Campos de EMPRESA (ADMIN) -------
      empresaNombre: ['', []],
      nit: ['', []],
      correoContacto: ['', []],
    });


    this.applyValidators('empleado');

    this.form.get('role')!.valueChanges.subscribe((role: Rol) => {
      this.applyValidators(role);
    });
  }

  get c() { return this.form.controls; }

  setRole(role: Rol) {
    this.form.patchValue({ role });
  }

  private applyValidators(role: Rol) {
    const clear = (names: string[]) =>
      names.forEach(n => {
        this.form.get(n)!.clearValidators();
        this.form.get(n)!.updateValueAndValidity({ emitEvent: false });
      });

    const set = (name: string, v: any[]) => {
      this.form.get(name)!.setValidators(v);
      this.form.get(name)!.updateValueAndValidity({ emitEvent: false });
    };

    if (role === 'empleado') {
      set('name', [Validators.required, Validators.minLength(2)]);
      set('email', [Validators.required, Validators.email]);
      set('password', [Validators.required, Validators.minLength(6)]);
      set('confirm', [Validators.required, Validators.minLength(6)]);
      set('empresaId', [Validators.required]);

      clear(['empresaNombre', 'nit', 'correoContacto']);
      this.form.patchValue({ empresaNombre: '', nit: '', correoContacto: '' });
    } else {
      set('empresaNombre', [Validators.required, Validators.minLength(2)]);
      set('nit', [Validators.required]);
      set('correoContacto', [Validators.required, Validators.email]);

      clear(['name', 'email', 'password', 'confirm', 'empresaId']);
      this.form.patchValue({ name: '', email: '', password: '', confirm: '', empresaId: null });
    }
  }

  samePassword(): boolean {
    const p = this.form.get('password')?.value;
    const c = this.form.get('confirm')?.value;
    return !!p && !!c && p === c;
  }

  onSubmit() {
    const role = this.form.value.role as Rol;

    if (role === 'empleado') {
      if (this.form.invalid || !this.samePassword()) {
        this.form.markAllAsTouched();
        return;
      }

      const payloadUsuario = {
        nombre: this.form.value.name,
        email: this.form.value.email,
        password: this.form.value.password,
        empresaId: Number(this.form.value.empresaId),
      };

      this.http.post(`${this.API}/usuario`, payloadUsuario).subscribe({
        next: () => this.router.navigateByUrl('/inicio-usuario'),
        error: (e) => console.error('Error al crear usuario', e),
      });
    } else {
      if (this.form.invalid) {
        this.form.markAllAsTouched();
        return;
      }

      const payloadEmpresa = {
        nombre: this.form.value.empresaNombre,
        nit: this.form.value.nit,
        correoContacto: this.form.value.correoContacto,
      };

      this.http.post(`${this.API}/empresa`, payloadEmpresa).subscribe({
        next: () => this.router.navigateByUrl('/inicio-admin'),
        error: (e) => console.error('Error al crear empresa', e),
      });
    }
  }
}
