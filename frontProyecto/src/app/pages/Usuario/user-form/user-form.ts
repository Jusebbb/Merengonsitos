import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { UsuarioService } from '../../../services/Usuario/usuario.services';
import { UsuarioDTO } from '../../../dtos/usuario.dto';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, FormsModule],
  templateUrl: './user-form.html',
  styleUrls: ['./user-form.scss']
})
export class UserFormComponent implements OnInit{

  userDto : UsuarioDTO = new UsuarioDTO;
  constructor(private usuarioService: UsuarioService,
    private router: Router
  ) {}

  ngOnInit(): void {
   
  }

  onSubmit() {
    this.crearUsuario();
  }

  crearUsuario(){
    this.usuarioService.createUser(this.userDto)
    .subscribe({
      next: (res)=>{
        console.log("Respuesta del servidor: ", res)
      },
      error:(err)=> {
        console.error("Error al crear empresa: ", err);
        alert('No se pudo crear el usuario. Revisa consola.');
      }
    });
  }
}
