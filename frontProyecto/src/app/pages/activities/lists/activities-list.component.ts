// activities-list.component.ts
import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { DragDropModule, CdkDragEnd } from '@angular/cdk/drag-drop';
import { ActivitiesService } from '../activities.service';
import { activityDto } from '../../../dtos/activityDto';

type Row = activityDto & { id?: number | string };

@Component({
  selector: 'app-activities-list',
  standalone: true,
  imports: [CommonModule, RouterModule, DragDropModule],
  templateUrl: './activities-list.component.html',
  styleUrls: ['./activities-list.component.scss']
})
export class ActivitiesListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private api = inject(ActivitiesService);
  constructor(private router: Router) {}

  processId = ''; // se mantiene por compatibilidad con tu HTML
  loading = signal(false);
  rows = signal<Row[]>([]);
  savingIds = signal<Set<number | string>>(new Set());

  pos = (a: Row) => ({
    x: Number.isFinite(a.x as number) ? (a.x as number) : 0,
    y: Number.isFinite(a.y as number) ? (a.y as number) : 0
  });

  ngOnInit() {
    // si tu ruta lleva /processes/:id/activities lo seguimos leyendo,
    // pero NO lo usamos para filtrar: traemos TODO.
    this.processId = this.route.parent?.snapshot.paramMap.get('id') ?? '';
    this.fetch();
  }

  fetch() {
  this.loading.set(true);
  this.api.listAll().subscribe({
    next: (data) => this.rows.set(data as any),
    error: (e) => console.error('Error listando actividades', e),
    complete: () => this.loading.set(false)
  });
}


  onDragEnded(a: Row, ev: CdkDragEnd) {
    const id = (a as any)?.id;
    if (id == null) return;
    const { x, y } = ev.source.getFreeDragPosition();

    this.rows.update(list => list.map(it => (it.id === id ? { ...it, x, y } : it)));
    this.savingIds.update(s => new Set([...s, id]));

    this.api.updatePosition(this.processId, id, { x, y }).subscribe({
      error: (e) => console.error('Error guardando posición', e),
      complete: () => {
        this.savingIds.update(s => { s.delete(id); return new Set(s); });
      }
    });
  }

  delete(a: Row) {
    const id = (a as any)?.id;
    if (id == null) {
      alert('No se puede eliminar: la actividad no tiene id.');
      return;
    }
    if (!confirm(`¿Eliminar actividad "${a.name}"?`)) return;
    this.api.delete(this.processId, id).subscribe({
      next: () => this.fetch(),
      error: (e) => console.error('Error eliminando', e)
    });
  }

  navegarNuevaActividad() {
    this.router.navigate(['actividad-form']);
  }
}
