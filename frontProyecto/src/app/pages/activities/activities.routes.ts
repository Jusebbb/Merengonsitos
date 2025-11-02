import { Routes } from '@angular/router';

export const ACTIVITIES_ROUTES: Routes = [
  {
    path: '', // Ruta base: /processes/:id/activities
    loadComponent: () =>
      import('./lists/activities-list.component')
      .then(m => m.ActivitiesListComponent)
  },
  {
    path: 'new', // Ruta para crear: /processes/:id/activities/new
    loadComponent: () =>
      import('./form/activity-form.component')
      .then(m => m.ActivityFormComponent)
  },
  {
    path: ':id/edit', // Ruta para editar: /processes/:id/activities/:activityId/edit
    loadComponent: () =>
      import('./form/activity-form.component')
      .then(m => m.ActivityFormComponent)
  }
];