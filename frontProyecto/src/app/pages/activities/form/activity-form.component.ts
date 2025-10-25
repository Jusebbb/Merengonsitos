import { Component, inject, OnInit } from '@angular/core'; // <<< Añadida la importación de OnInit
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ActivitiesService } from '../activities.service';
import { ActivityDTO } from '../../../dtos/activityDto';

@Component({
  selector: 'app-activity-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './activity-form.component.html',
  styleUrls: ['./activity-form.component.scss']
})
export class ActivityFormComponent implements OnInit { // La clase implementa OnInit :D
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private api = inject(ActivitiesService);

  processId = '';
  activityId: string | null = null;
  isEdit = false;

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    type: ['TASK', Validators.required], // TASK | EVENT | SUBPROCESS
    description: [''],
    responsibleRole: ['']
  });

  ngOnInit() {
    this.processId = this.route.parent!.snapshot.paramMap.get('id')!;
    this.activityId = this.route.snapshot.paramMap.get('id');
    this.isEdit = !!this.activityId;

    if (this.isEdit && this.activityId) {
      this.api.getById(this.processId, this.activityId).subscribe((a: any) => {
        this.form.patchValue({
          name: a.name,
          type: a.type,
          description: a.description ?? '',
          responsibleRole: a.responsibleRole ?? ''
        });
      });
    }
  }

  save() {
    const body = this.form.value as Partial<ActivityDTO>;

    if (this.isEdit && this.activityId) {
      // HU-09: Actualizar
      this.api.update(this.processId, this.activityId, body)
        .subscribe(() => this.router.navigate(['/processes', this.processId, 'activities']));
    } else {
      // HU-09: Crear
      this.api.create(this.processId, body)
        .subscribe(() => this.router.navigate(['/processes', this.processId, 'activities']));
    }
  }
}
