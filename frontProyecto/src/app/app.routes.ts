import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { RegisterComponent } from './pages/register/register';
import { InicioAdminComponent } from './pages/inicio-admin/inicio-admin';
import { InicioEmpleadoComponent } from './pages/inicio-empleado/inicio-empleado';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'inicio-admin', component: InicioAdminComponent },
  { path: 'inicio-usuario', component: InicioEmpleadoComponent },
  { path: '**', redirectTo: 'login' },
];
