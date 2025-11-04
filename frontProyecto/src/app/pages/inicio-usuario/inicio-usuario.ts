import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-inicio-usuario',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './inicio-usuario.html',  
  styleUrls: ['./inicio-usuario.scss'], 
})
export class InicioUsuarioComponent implements OnInit{
constructor(private router: Router) {}

  ngOnInit(): void {}

  navegarProcesos(): void {
    this.router.navigate(['/proceso-list']);
  }

  navegarActividades(): void {
    this.router.navigate(['/actividad-list']);
  }

  navegarGateways(): void {
    // No hay ruta explícita de gateways; redirijo a procesos con un queryParam
    this.router.navigate(['/proceso-list'], { queryParams: { section: 'gateways' } });
  }

  // Extras para que TODOS los botones funcionen:
  navegarUsuarios(): void {
    // Tienes 'crear-usuario' en rutas
    this.router.navigate(['/crear-usuario']);
  }

  navegarRoles(): void {
    // No hay ruta de roles; envío a inicio-admin con marca de sección
    this.router.navigate(['/inicio-admin'], { queryParams: { section: 'roles' } });
  }
}
