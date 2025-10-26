import { Component } from '@angular/core';
import { Router } from '@angular/router';  // Asegúrate de importar Router
import { RouterModule } from '@angular/router';  // Asegúrate de importar RouterModule

@Component({
  selector: 'app-inicio-admin',
  standalone: true,  // Esto hace que el componente sea independiente
  imports: [RouterModule],  // Asegúrate de importar RouterModule aquí
  templateUrl: './inicio-admin.html',
  styleUrls: ['./inicio-admin.scss'],
})
export class InicioAdminComponent {
  constructor(private router: Router) {}

  goToCreateUser() {
    this.router.navigate(['/crear-usuario']);  // Redirige a la página de creación de usuario
  }
}
