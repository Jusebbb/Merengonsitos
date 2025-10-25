import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder, Validators, ReactiveFormsModule, FormGroup, AbstractControl, ValidationErrors
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

type Rol = 'empleado' | 'administrador';

// Validador cruzado para confirmar contraseña (solo aplica a empleado)
function samePasswordGroup(ctrl: AbstractControl): ValidationErrors | null {
  const p = ctrl.get('password')?.value;
  const c = ctrl.get('confirm')?.value;
  return p && c && p !== c ? { passwordMismatch: true } : null;
}

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
    this.form = this.fb.group({
      role: ['empleado' as Rol, [Validators.required]],

      // ------- EMPLEADO -------
      userNombre: [''],
      email: [''],
      password: [''],
      confirm: [''],
      empresaId: [null],

      // ------- EMPRESA / ADMIN -------
      nombre: [''],
      nit: [''],
      correoContacto: [''],
    });

    // Validaciones iniciales para empleado
    this.applyValidators('empleado');

    // Reaccionar al cambio de rol
    this.form.get('role')!.valueChanges.subscribe((role: Rol) => {
      this.applyValidators(role);
    });
  }

  get c() { return this.form.controls; }

  setRole(role: Rol) {
    this.form.patchValue({ role });
  }

  /** Aplica/limpia validadores según el rol y setea validador cruzado cuando toca */
  private applyValidators(role: Rol) {
    const clear = (names: string[]) => names.forEach(n => {
      this.form.get(n)!.clearValidators();
      this.form.get(n)!.updateValueAndValidity({ emitEvent: false });
    });

    const set = (name: string, v: any[]) => {
      this.form.get(name)!.setValidators(v);
      this.form.get(name)!.updateValueAndValidity({ emitEvent: false });
    };

    // Limpia validador cruzado por defecto
    this.form.clearValidators();

    if (role === 'empleado') {
      set('userNombre', [Validators.required, Validators.minLength(2)]);
      set('email', [Validators.required, Validators.email]);
      set('password', [Validators.required, Validators.minLength(6)]);
      set('confirm', [Validators.required, Validators.minLength(6)]);
      set('empresaId', [Validators.required, Validators.min(1)]);

      clear(['nombre', 'nit', 'correoContacto']);
      this.form.patchValue({ nombre: '', nit: '', correoContacto: '' });

      // Validador cruzado solo para empleado
      this.form.setValidators(samePasswordGroup);
    } else {
      set('nombre', [Validators.required, Validators.minLength(2)]);
      set('nit', [Validators.required]);
      set('correoContacto', [Validators.required, Validators.email]);

      clear(['userNombre', 'email', 'password', 'confirm', 'empresaId']);
      this.form.patchValue({ userNombre: '', email: '', password: '', confirm: '', empresaId: null });
    }

    this.form.updateValueAndValidity({ emitEvent: false });
  }

  onSubmit() {
    const role = this.c['role'].value as Rol;

    // Validación general
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    // Extra: si es empleado y no coinciden pass
    if (role === 'empleado' && this.form.errors?.['passwordMismatch']) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;

    if (role === 'empleado') {
      // Mapeo a UsuarioDTO: nombre <- userNombre
      const payloadUsuario = {
        nombre: this.c['userNombre'].value,
        email: this.c['email'].value,
        password: this.c['password'].value,
        empresaId: Number(this.c['empresaId'].value),
      };

      this.http.post(`${this.API}/usuario`, payloadUsuario).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigateByUrl('/inicio-usuario');
        },
        error: (e) => {
          this.loading = false;
          console.error('Error al crear usuario', e);
          alert(e?.error?.message || 'No se pudo crear el usuario');
        },
      });
    } else {
      
      const payloadEmpresa = {
        nombre: this.c['nombre'].value,
        nit: this.c['nit'].value,
        correoContacto: this.c['correoContacto'].value,
      };

      this.http.post(`${this.API}/empresas`, payloadEmpresa).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigateByUrl('/inicio-admin');
        },
        error: (e) => {
          this.loading = false;
          console.error('Error al crear empresa', e);
          alert(e?.error?.message || 'No se pudo crear la empresa');
        },
      });
    }
  }
}
