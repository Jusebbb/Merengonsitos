import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/Login/auth.service';
import { LoginDto } from '../../dtos/loginDto';
import { UsuarioService } from '../../services/Usuario/usuario.services';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss'],
})
export class LoginComponent implements OnInit {

  loginDto: LoginDto = new LoginDto();

  constructor(
    private loginService: AuthService,
    private router: Router,
    private usuarioService: UsuarioService
  ) {}

  ngOnInit(): void {}

  onSubmit() {
    console.log('Correo:', this.loginDto.email);
    console.log('Contraseña:', this.loginDto.password);
    this.loginUser();
  }

  loginUser() {
    this.loginService.loginSolv(this.loginDto).subscribe(
      (data) => {
        if (data) {
          this.navegarHomeScreen();
        } else {
          alert('Las credenciales no coinciden, intenta de nuevo.');
        }
      },
      (error) => {
        console.error('Error al iniciar sesión:', error);
        alert('Error al iniciar sesión. Revisa tus credenciales.');
      }
    );
  }

  navegarHomeScreen() {
    this.router.navigate(['inicio-usuario']);
  }
}
