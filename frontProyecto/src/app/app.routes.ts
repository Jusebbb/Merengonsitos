import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { RegisterComponent } from './pages/register/register';
import { InicioAdminComponent } from './pages/inicio-admin/inicio-admin';
import { InicioUsuarioComponent } from './pages/inicio-usuario/inicio-usuario';
import { UserFormComponent } from './pages/Usuario/user-form/user-form';  // Asegúrate de importar correctamente

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'inicio-admin', component: InicioAdminComponent },
  { path: 'crear-usuario', component: UserFormComponent },  // Esta es la ruta correcta
  { path: 'inicio-usuario', component: InicioUsuarioComponent },
  { path: '**', redirectTo: 'login' }
];
