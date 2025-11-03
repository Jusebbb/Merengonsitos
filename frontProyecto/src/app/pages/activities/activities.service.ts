    import { Injectable, inject } from '@angular/core';
    import { HttpClient } from '@angular/common/http';
    import { Observable } from 'rxjs'; 
    import { ActivityDTO } from '../../dtos/activityDto'; // usa tu DTO existente

    @Injectable({ providedIn: 'root' })
    export class ActivitiesService { 
    private http = inject(HttpClient);
    private base = '/api/processes'; 

    // HU-08: listar actividades de un proceso 
    listByProcess(processId: string): Observable<ActivityDTO[]> { 
        return this.http.get<ActivityDTO[]>(`${this.base}/${processId}/activities`); 
    } 

    // HU-09: crear nueva actividad 
    create(processId: string, body: Partial<ActivityDTO>): Observable<ActivityDTO> { 
        return this.http.post<ActivityDTO>(`${this.base}/${processId}/activities`, body); 
    } 

    // HU-09: actualizar una actividad 
    update(processId: string, id: string, body: Partial<ActivityDTO>): Observable<ActivityDTO> { 
        return this.http.put<ActivityDTO>(`${this.base}/${processId}/activities/${id}`, body); 
    } 
    
    // HU-10: eliminar una actividad 
    delete(processId: string, id: string): Observable<void> { 
        return this.http.delete<void>(`${this.base}/${processId}/activities/${id}`); 
    } 
    
    // obtener una actividad por id 
    getById(processId: string, id: string): Observable<ActivityDTO> { 
        return this.http.get<ActivityDTO>(`${this.base}/${processId}/activities/${id}`); 
    } 
    }