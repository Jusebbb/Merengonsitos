import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, Validators, NonNullableFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ProcessesService } from '../processes.service';
import { ProcessDTO, ProcessStatus } from '../../../dtos/processDto';

@Component({
  selector: 'app-process-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './process-form.component.html',
  styleUrls: ['./process-form.component.scss']
})
export class ProcessFormComponent {
  private fb = inject(NonNullableFormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private api = inject(ProcessesService);

  isEdit = false;
  current?: ProcessDTO;

  form = this.fb.group({
    name: this.fb.control<string>('', { validators: [Validators.required, Validators.minLength(3)] }),
    description: this.fb.control<string>(''),
    status: this.fb.control<ProcessStatus>('DRAFT', { validators: [Validators.required] })
  });

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.api.getById(id).subscribe(p => {
        this.current = p;
        this.form.setValue({
          name: p.name ?? '',
          description: p.description ?? '',
          status: p.status
        });
      });
    }
  }

  save() {
    const payload = this.form.getRawValue();

    if (this.isEdit && this.current) {
      this.api.update(this.current.id, payload).subscribe(() => this.router.navigate(['/processes']));
    } else {
      this.api.create(payload).subscribe(() => this.router.navigate(['/processes']));
    }
  }
}
