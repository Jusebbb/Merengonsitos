import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-inicio-admin',
  standalone: true,
  imports: [CommonModule, RouterModule], // ← necesario para routerLink
  templateUrl: './inicio-admin.html',
  styleUrls: ['./inicio-admin.scss'],
})
export class InicioAdminComponent {}
