import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { ActivitiesService } from '../activities.service';
import { ActivityDTO } from '../../../dtos/activityDto';

@Component({
  selector: 'app-activities-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './activities-list.component.html',
  styleUrls: ['./activities-list.component.scss']
})
export class ActivitiesListComponent implements OnInit { // Implementamos OnInit :D
  private route = inject(ActivatedRoute);
  private api = inject(ActivitiesService);

  processId = '';
  loading = signal(false);
  rows = signal<ActivityDTO[]>([]);

  ngOnInit() {
    this.processId = this.route.parent!.snapshot.paramMap.get('id')!;
    this.fetch();
  }

  fetch() {
    this.loading.set(true);
    // HU-08: Listar
    this.api.listByProcess(this.processId).subscribe({
      next: (data: ActivityDTO[]) => this.rows.set(data),
      complete: () => this.loading.set(false)
    });
  }

  delete(a: ActivityDTO) {
    // HU-10: Eliminar
    if (!confirm(`¿Eliminar actividad "${a.name}"?`)) return;
    this.api.delete(this.processId, a.id).subscribe(() => this.fetch());
  }
}