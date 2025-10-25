import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder, Validators, ReactiveFormsModule, FormGroup, AbstractControl, ValidationErrors
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { EmpresaService } from '../../services/empresa.services';
import { UsuarioService } from '../../services/usuario.services';

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

  constructor(
    private fb: FormBuilder,
    private empresaSrv: EmpresaService,
    private usuarioSrv: UsuarioService,
    private router: Router
  ) {
    this.form = this.fb.group({
      role: ['administrador' as Rol, [Validators.required]],

      // EMPLEADO
      userNombre: [''],
      email: [''],
      password: [''],
      confirm: [''],
      empresaId: [null],

      // EMPRESA (ADMIN)
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      nit: ['', [Validators.required]],
      correoContacto: ['', [Validators.required, Validators.email]],

      // Campo visual (solo UI, NO se envía)
      empresaPassword: [''],
    });

    this.form.get('role')!.valueChanges.subscribe((role: Rol) => {
      this.applyValidators(role);
    });

    // aplica validación inicial para el rol por defecto
    this.applyValidators(this.form.get('role')!.value as Rol);
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

    // Limpia validador de grupo
    this.form.clearValidators();

    if (role === 'empleado') {
      // Validaciones PARA EMPLEADO
      set('userNombre', [Validators.required, Validators.minLength(2)]);
      set('email', [Validators.required, Validators.email]);
      set('password', [Validators.required, Validators.minLength(6)]);
      set('confirm', [Validators.required, Validators.minLength(6)]);
      set('empresaId', [Validators.required, Validators.min(1)]);
      this.form.setValidators(samePasswordGroup);

      // Limpia empresa
      clear(['nombre', 'nit', 'correoContacto']);

    } else {
      // Validaciones PARA ADMIN (empresa)
      set('nombre', [Validators.required, Validators.minLength(2)]);
      set('nit', [Validators.required]);
      set('correoContacto', [Validators.required, Validators.email]);

      // Limpia empleado
      clear(['userNombre', 'email', 'password', 'confirm', 'empresaId']);
    }

    this.form.updateValueAndValidity({ emitEvent: false });
  }

  onSubmit() {
    const role = this.c['role'].value as Rol;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      if (role === 'empleado' && this.form.errors?.['passwordMismatch']) {
        // mantén el mensaje visual en el template si lo deseas
      }
      return;
    }

    this.loading = true;

    if (role === 'empleado') {
      // === Crear USUARIO (empleado) ===
      const payload = {
        nombre: String(this.c['userNombre'].value || '').trim(),
        email: String(this.c['email'].value || '').trim(),
        password: String(this.c['password'].value || '').trim(),
        empresaId: Number(this.c['empresaId'].value),
      };

      this.usuarioSrv.crear(payload).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigateByUrl('/inicio-usuario');
        },
        error: (e) => {
          this.loading = false;
          console.error('Error al crear usuario', e);
          alert(e?.error?.message || `HTTP ${e?.status}` || 'No se pudo crear el usuario');
        },
      });

    } else {
      // === Crear EMPRESA (administrador) ===
      const payloadEmpresa = {
        nombre: String(this.c['nombre'].value || '').trim(),
        nit: String(this.c['nit'].value || '').trim(),
        correoContacto: String(this.c['correoContacto'].value || '').trim(),
      };

      this.empresaSrv.crear(payloadEmpresa).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigateByUrl('/inicio-admin');
        },
        error: (e) => {
          this.loading = false;
          console.error('Error al crear empresa', e);
          alert(e?.error?.message || `HTTP ${e?.status}` || 'No se pudo crear la empresa');
        },
      });
    }
  }
}
