import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';  // Importa RouterModule para la navegación
import { HttpClientModule } from '@angular/common/http';
import { UsuarioService } from '../../../services/usuario.services';  // Asegúrate de importar tu servicio

@Component({
  selector: 'app-user-form',
  standalone: true,  // Esto hace que el componente sea independiente
  imports: [CommonModule, ReactiveFormsModule, RouterModule, HttpClientModule],  // Importa los módulos necesarios
  templateUrl: './user-form.html',
  styleUrls: ['./user-form.scss']
})
export class UserFormComponent {
  userForm!: FormGroup;

  constructor(private fb: FormBuilder, private usuarioService: UsuarioService) { }

  ngOnInit(): void {
    // Inicializamos el formulario reactivo
    this.userForm = this.fb.group({
      nombre: ['', Validators.required],
      correo: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rol: ['', Validators.required]
    });
  }

  // Función para manejar el envío del formulario
  onSubmit() {
    if (this.userForm.invalid) {
      return;
    }

    const usuario = this.userForm.value;
    this.usuarioService.createUser(usuario).subscribe(
      response => {
        // Redirigir o mostrar un mensaje de éxito
        console.log('Usuario creado:', response);
      },
      error => {
        console.error('Error al crear el usuario:', error);
      }
    );
  }
}
