import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
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

  processId = '';
  loading = signal(false);
  rows = signal<Row[]>([]);
  savingIds = signal<Set<number | string>>(new Set()); // para marcar tarjetas guardando

  // fallback si vienen null/undefined
  pos = (a: Row) => ({
    x: Number.isFinite(a.x as number) ? (a.x as number) : 0,
    y: Number.isFinite(a.y as number) ? (a.y as number) : 0
  });

  ngOnInit() {
    this.processId = this.route.parent!.snapshot.paramMap.get('id')!;
    this.fetch();
  }

  fetch() {
    this.loading.set(true);
    this.api.listByProcess(this.processId).subscribe({
      next: (data: activityDto[]) => this.rows.set(data as Row[]),
      complete: () => this.loading.set(false)
    });
  }

  onDragEnded(a: Row, ev: CdkDragEnd) {
    const id = (a as any)?.id;
    if (id == null) {
      // sin id no persistimos
      return;
    }
    const { x, y } = ev.source.getFreeDragPosition(); // posición absoluta actual
    // Optimista: actualiza la signal para que la tarjeta “salte” al lugar guardado
    this.rows.update(list =>
      list.map(it => (it.id === id ? { ...it, x, y } : it))
    );

    // Marcar guardando
    this.savingIds.update(s => new Set([...s, id]));

    // Llama a tu endpoint de update/patch de posición (ajusta el método según tu service)
    this.api.updatePosition(this.processId, id, { x, y }).subscribe({
      next: () => {
        // ok
      },
      error: () => {
        // rollback simple: si quieres, podrías hacer refetch(); aquí lo dejamos simple
      },
      complete: () => {
        this.savingIds.update(s => {
          s.delete(id);
          return new Set(s);
        });
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
    this.api.delete(this.processId, id).subscribe(() => this.fetch());
  }
}
