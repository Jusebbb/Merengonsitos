import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ProcessesService } from '../processes.service';
import { ProcessDTO } from '../../dtos/processDto';

@Component({
  selector: 'app-processes-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './processes-list.component.html',
  styleUrls: ['./processes-list.component.scss']
})
export class ProcessesListComponent {
  private api = inject(ProcessesService);

  q = '';
  status = '';
  loading = signal(false);
  rows = signal<ProcessDTO[]>([]);

  ngOnInit() { this.fetch(); }

  fetch() {
    this.loading.set(true);
    this.api.list({ q: this.q, status: this.status }).subscribe({
      next: data => this.rows.set(data),
      complete: () => this.loading.set(false)
    });
  }

  onDelete(p: ProcessDTO) {
    if (!confirm(`Eliminar proceso "${p.name}"?`)) return;
    this.api.delete(p.id).subscribe(() => this.fetch());
  }
}
