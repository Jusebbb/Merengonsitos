import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { activityDto } from '../../dtos/activityDto';

export type UpdatePositionPayload = { x: number; y: number };

@Injectable({ providedIn: 'root' })
export class ActivitiesService {
  private http = inject(HttpClient);

  private activitiesBase = '/api/activities';   // <- ActivityController
  private processesBase  = '/api/processes';    // <- ProcessController (si lo necesitas en otros servicios)

  // HU-08: listar actividades de un proceso
  listByProcess(processId: string): Observable<activityDto[]> {
    // GET /api/activities/by-process/{processId}
    return this.http.get<activityDto[]>(
      `${this.activitiesBase}/by-process/${processId}`
    );
  }

  // HU-09: crear nueva actividad
  create(_processId: string, body: Partial<activityDto>): Observable<activityDto> {
    // POST /api/activities  (el processId viaja en el body, tu DTO lo exige)
    return this.http.post<activityDto>(`${this.activitiesBase}`, body);
  }

  // HU-09: actualizar una actividad
  update(_processId: string, id: string, body: Partial<activityDto>): Observable<activityDto> {
    // PUT /api/activities/{id}
    return this.http.put<activityDto>(`${this.activitiesBase}/${id}`, body);
  }

  // HU-10: eliminar una actividad (soft delete en tu controller)
  delete(_processId: string, id: string): Observable<void> {
    // DELETE /api/activities/{id}
    return this.http.delete<void>(`${this.activitiesBase}/${id}`);
  }

  // obtener una actividad por id (si luego agregas GET /api/activities/{id})
  getById(_processId: string, id: string): Observable<activityDto> {
    return this.http.get<activityDto>(`${this.activitiesBase}/${id}`);
  }

  /** Guarda solo la posición (x,y) de la actividad */
  updatePosition(
    _processId: string | number,
    id: string | number,
    pos: UpdatePositionPayload
  ): Observable<activityDto> {
    // PATCH /api/activities/{id}/position  Body: { x, y }
    return this.http.patch<activityDto>(
      `${this.activitiesBase}/${id}/position`,
      pos
    );
  }

  // --- Alternativas por si tu backend cambia en el futuro ---

  /** Si alguna vez expusieras PATCH general en /api/activities/{id} */
  updatePositionAlt(id: string | number, pos: UpdatePositionPayload) {
    return this.http.patch<activityDto>(`${this.activitiesBase}/${id}`, pos);
  }

  /** Si más adelante cuelgas activities bajo processes */
  updatePositionUnderProcess(
    processId: string | number,
    id: string | number,
    pos: UpdatePositionPayload
  ) {
    return this.http.patch<activityDto>(
      `${this.processesBase}/${processId}/activities/${id}/position`,
      pos
    );
  }
}
