import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrls: ['./register.scss'],
})
export class RegisterComponent {
  form!: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirm: ['', [Validators.required, Validators.minLength(6)]],
      role: ['empleado', [Validators.required]],
    });
  }

  get c() { return this.form.controls; }

  setRole(role: 'administrador' | 'empleado') {
    this.form.get('role')?.setValue(role);
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
    console.log('REGISTER =>', {
      name: this.form.value.name,
      email: this.form.value.email,
      password: this.form.value.password,
      role: this.form.value.role,                             // 👈 incluido
    });
  }
}
