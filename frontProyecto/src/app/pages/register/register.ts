import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrls: ['./register.scss'],
})
export class RegisterComponent {
  form!: FormGroup;

  constructor(private fb: FormBuilder, private router: Router) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirm: ['', [Validators.required, Validators.minLength(6)]],
      role: ['empleado', [Validators.required]],
    });
  }

  get c() { return this.form.controls; }

  private setRole(role: 'empleado' | 'administrador') {
    this.form.patchValue({ role });
  }

  // Navega inmediatamente según el botón pulsado
  goToRole(role: 'empleado' | 'administrador') {
    this.setRole(role);
    this.router.navigateByUrl(role === 'administrador' ? '/inicio-admin' : '/inicio-usuario');
  }

  samePassword(): boolean {
    const p = this.form.get('password')?.value;
    const c = this.form.get('confirm')?.value;
    return !!p && !!c && p === c;
  }

  onSubmit() {
    if (this.form.invalid || !this.samePassword()) {
      this.form.markAllAsTouched();
      return;
    }

    // Demo: podrías mandar el payload al backend aquí.
    console.log('REGISTER =>', this.form.value);

    // Redirige según el rol seleccionado
    const role = this.form.value.role as 'empleado' | 'administrador';
    this.router.navigateByUrl(role === 'administrador' ? '/inicio-admin' : '/inicio-usuario');
  }
}
