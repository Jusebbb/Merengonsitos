import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { activityDto } from '../../dtos/activityDto';

export type UpdatePositionPayload = { x: number; y: number };

@Injectable({ providedIn: 'root' })
export class ActivitiesService {
  private http = inject(HttpClient);

  /** 👇 Usa la URL completa del backend */
  private apiBase = 'http://localhost:8080/api/activities';

  /** Listar todas las actividades (GET /api/activities) */
  listAll(): Observable<activityDto[]> {
    return this.http.get<activityDto[]>(`${this.apiBase}`);
  }

  /** Listar por proceso (GET /api/activities/by-process/{processId}) */
  listByProcess(processId: string): Observable<activityDto[]> {
    return this.http.get<activityDto[]>(`${this.apiBase}/by-process/${processId}`);
  }

  /** Crear nueva (POST /api/activities) */
  create(_processId: string, body: Partial<activityDto>): Observable<activityDto> {
    return this.http.post<activityDto>(`${this.apiBase}`, body);
  }

  /** Actualizar (PUT /api/activities/{id}) */
  update(_processId: string, id: string, body: Partial<activityDto>): Observable<activityDto> {
    return this.http.put<activityDto>(`${this.apiBase}/${id}`, body);
  }

  /** Eliminar lógico (DELETE /api/activities/{id}) */
  delete(_processId: string, id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiBase}/${id}`);
  }

  /** Obtener por id (GET /api/activities/{id}) */
  getById(_processId: string, id: string): Observable<activityDto> {
    return this.http.get<activityDto>(`${this.apiBase}/${id}`);
  }

  /** Guardar posición (PATCH /api/activities/{id}/position) */
  updatePosition(_processId: string | number, id: string | number, pos: UpdatePositionPayload): Observable<activityDto> {
    return this.http.patch<activityDto>(`${this.apiBase}/${id}/position`, pos);
  }
}

