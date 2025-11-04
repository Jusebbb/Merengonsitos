import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { RegisterComponent } from './pages/register/register';
import { InicioAdminComponent } from './pages/inicio-admin/inicio-admin';
import { InicioUsuarioComponent } from './pages/inicio-usuario/inicio-usuario';
import { UserFormComponent } from './pages/Usuario/user-form/user-form'; 
import { ProcessesListComponent } from './pages/processes/lists/processes-list.component';
import { ActivityFormComponent } from './pages/activities/form/activity-form.component';
import { ProcessViewComponent } from './pages/processes/view/process-view.component';
import { ProcessFormComponent } from './pages/processes/form/process-form.component';
import { ActivitiesListComponent } from './pages/activities/lists/activities-list.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'inicio-admin', component: InicioAdminComponent },
  { path: 'crear-usuario', component: UserFormComponent },
  { path: 'inicio-usuario', component: InicioUsuarioComponent },
  { path: 'proceso-list', component:ProcessesListComponent},
  { path: 'actividad-list', component: ActivitiesListComponent },
  // { path: 'processes/:id/activities', component: ActivitiesListComponent }, 
  // { path: 'actividad-list', component:ActivitiesListComponent},
  { path: 'proceso-view', component: ProcessViewComponent},
  { path: 'proceso-form', component: ProcessFormComponent},
  { path: 'actividad-form',component: ActivityFormComponent},
  { path: '**', redirectTo: 'login' }
];
