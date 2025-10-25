import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder, Validators, ReactiveFormsModule, FormGroup, AbstractControl, ValidationErrors
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';

type Rol = 'empleado' | 'administrador';

// Validador cruzado para confirmar contraseña (rama empleado)
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

      // Campo visual de contraseña (no se envía)
      empresaPassword: [''],
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

      clear(['userNombre', 'email', 'password', 'confirm', 'empresaId']);
    }

    this.form.updateValueAndValidity({ emitEvent: false });
  }

  onSubmit() {
    const role = this.c['role'].value as Rol;
    console.log('[REGISTER] submit role=', role, 'valid=', this.form.valid, 'value=', this.form.value);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      console.log('[REGISTER] form INVALID');
      return;
    }

    this.loading = true;

    if (role === 'administrador') {
      // SOLO crear la EMPRESA
      const payloadEmpresa = {
        nombre: String(this.c['nombre'].value || '').trim(),
        nit: String(this.c['nit'].value || '').trim(),
        correoContacto: String(this.c['correoContacto'].value || '').trim(),
      };

      console.log('POST /api/empresas =>', payloadEmpresa);

      const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

      this.http.post(`${this.API}/empresas`, payloadEmpresa, {
        headers,
        observe: 'response',
        responseType: 'json'
      }).subscribe({
        next: (res) => {
          console.log('OK /api/empresas =>', res.status, res.body);
          this.loading = false;
          this.router.navigateByUrl('/inicio-admin');
        },
        error: (e) => {
          this.loading = false;
          // Muestra el error real del backend
          console.error('ERR /api/empresas =>', e);
          console.log('ERR body =>', e?.error);
          const msg =
            e?.error?.message ||
            e?.error?.error ||
            (typeof e?.error === 'string' && e.error) ||
            (e?.error?.errors && Object.values(e.error.errors).flat().join('\n')) ||
            (e?.status ? `HTTP ${e.status}` : '') ||
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
