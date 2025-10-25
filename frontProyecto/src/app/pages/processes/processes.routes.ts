// src/app/processes/processes.routes.ts
import { Routes } from '@angular/router';

export const PROCESSES_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./lists/processes-list.component')
        .then(m => m.ProcessesListComponent)
  },
  {
    path: 'new',
    loadComponent: () =>
      import('./form/process-form.component')
        .then(m => m.ProcessFormComponent)
  },
  {
    path: ':id/edit',
    loadComponent: () =>
      import('./form/process-form.component')
        .then(m => m.ProcessFormComponent)
  },
  {
  path: ':id',
  loadComponent: () =>
    import('./view/process-view.component')
      .then(m => m.ProcessViewComponent),
  children: [
    {
      path: 'activities',
      loadChildren: () =>
         import('../activities/activities.routes').then(m => m.ACTIVITIES_ROUTES)
    }
  ]
}
];
