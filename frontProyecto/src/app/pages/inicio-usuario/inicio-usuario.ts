import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-inicio-usuario',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './inicio-usuario.html',   // <- en minúsculas
  styleUrls: ['./inicio-usuario.scss'],   // <- en minúsculas
})
export class InicioUsuarioComponent {}
