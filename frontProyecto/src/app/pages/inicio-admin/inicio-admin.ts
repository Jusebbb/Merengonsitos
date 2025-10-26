import { Component } from '@angular/core';
import { Router } from '@angular/router';  // Correcto: Importar Router desde '@angular/router'

@Component({
  selector: 'app-inicio-admin',
  standalone: true,  // Esto hace que el componente sea independiente
  imports: [],  // Ya no necesitas importar Router aquí
  templateUrl: './inicio-admin.html',
  styleUrls: ['./inicio-admin.scss'],
})
export class InicioAdminComponent {
  constructor(private router: Router) {}

  goToCreateUser() {
    this.router.navigate(['/crear-usuario']);
  }
}
