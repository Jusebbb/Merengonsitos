import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { UsuarioService } from '../../../services/Usuario/usuario.services';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './user-form.html',
  styleUrls: ['./user-form.scss']
})
export class UserFormComponent {
  userForm!: FormGroup;
  loading = false;
  okMsg = '';
  errorMsg = '';

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userForm = this.fb.group({
      nombre: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rol: ['LECTOR', Validators.required] // enum en MAYÚSCULAS
    });
  }

  onSubmit() {
    this.okMsg = '';
    this.errorMsg = '';

    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    const empresaId = Number(localStorage.getItem('empresaId')); // viene del login
    if (!empresaId) {
      this.errorMsg = 'No se encontró la empresa asociada al usuario actual.';
      return;
    }

    this.loading = true;

    const raw = this.userForm.getRawValue();
    const dto = {
      ...raw,
      rol: String(raw.rol).toUpperCase(), // asegurar mayúsculas
      empresaId
    };

    this.usuarioService.createUser(dto as any).subscribe({
      next: (res) => {
        this.loading = false;
        this.okMsg = `Usuario creado (id ${res.id}).`;
        this.userForm.reset({ rol: 'LECTOR' });
        // Si prefieres redirigir automáticamente, descomenta:
        // this.router.navigate(['/inicio-admin']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMsg = err?.error?.message || 'No se pudo crear el usuario.';
      }
    });
  }

  // Botón "Volver al inicio"
  goInicioAdmin() {
    this.router.navigate(['/inicio-admin']);
  }
}
