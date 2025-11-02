import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ProcessesService } from '../processes.service';
import { ProcessDTO } from '../../../dtos/processDto';

@Component({
  selector: 'app-process-view',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './process-view.component.html',
  styleUrls: ['./process-view.component.scss']
})
export class ProcessViewComponent {
  private route = inject(ActivatedRoute);
  private api = inject(ProcessesService);
  process?: ProcessDTO;

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.api.getById(id).subscribe(p => this.process = p);
  }
}
