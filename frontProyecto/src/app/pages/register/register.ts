import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder, Validators, ReactiveFormsModule, FormGroup, AbstractControl, ValidationErrors
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';

type Rol = 'empleado' | 'administrador';

// Validador cruzado para confirmar contraseña (para empleados)
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
      role: ['administrador' as Rol, [Validators.required]],

      // EMPLEADO (pendiente)
      userNombre: [''],
      email: [''],
      password: [''],
      confirm: [''],
      empresaId: [null],

      // EMPRESA (ADMIN)
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      nit: ['', [Validators.required]],
      correoContacto: ['', [Validators.required, Validators.email]],

      // Campo visual de contraseña (se enviará al backend)
      empresaPassword: ['', [Validators.required]], // Added Validators for password
    });

    // Validaciones dinámicas si cambian de rol
    this.form.get('role')!.valueChanges.subscribe((role: Rol) => {
      this.applyValidators(role);
    });
  }

  get c() { return this.form.controls; }

  setRole(role: Rol) {
    this.form.patchValue({ role });
    this.applyValidators(role);
  }

  private applyValidators(role: Rol) {
    const clear = (names: string[]) => names.forEach(n => {
      this.form.get(n)!.clearValidators();
      this.form.get(n)!.updateValueAndValidity({ emitEvent: false });
    });
    const set = (name: string, v: any[]) => {
      this.form.get(name)!.setValidators(v);
      this.form.get(name)!.updateValueAndValidity({ emitEvent: false });
    };

    // Limpia validadores del form group
    this.form.clearValidators();

    if (role === 'empleado') {
      set('userNombre', [Validators.required, Validators.minLength(2)]);
      set('email', [Validators.required, Validators.email]);
      set('password', [Validators.required, Validators.minLength(6)]);
      set('confirm', [Validators.required, Validators.minLength(6)]);
      set('empresaId', [Validators.required, Validators.min(1)]);
      this.form.setValidators(samePasswordGroup);

      clear(['nombre', 'nit', 'correoContacto']);
    } else {
      // ADMIN → solo empresa (EmpresaDTO: nombre, nit, correoContacto)
      set('nombre', [Validators.required, Validators.minLength(2)]);
      set('nit', [Validators.required]);
      set('correoContacto', [Validators.required, Validators.email]);
      set('empresaPassword', [Validators.required]);  // Added password validation for admin

      clear(['userNombre', 'email', 'password', 'confirm', 'empresaId']);
    }

    this.form.updateValueAndValidity({ emitEvent: false });
  }

  onSubmit() {
    const role = this.c['role'].value as Rol;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;

    if (role === 'administrador') {
      // Crear la EMPRESA y enviar la contraseña
      const payloadEmpresa = {
        nombre: String(this.c['nombre'].value || '').trim(),
        nit: String(this.c['nit'].value || '').trim(),
        correoContacto: String(this.c['correoContacto'].value || '').trim(),
        password: String(this.c['empresaPassword'].value || '').trim(),  // Enviar la contraseña
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

    } else {
      this.loading = false;
      alert('Modo EMPLEADO pendiente de implementar.');
    }
  }
}
