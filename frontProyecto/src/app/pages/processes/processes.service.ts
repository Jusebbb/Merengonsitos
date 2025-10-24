import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ProcessDTO } from '../../dtos/processDto';
import { ActivityDTO } from '../../dtos/activityDto';
import { EdgeDTO } from '../../dtos/edgeDto';
import { GatewayDTO } from '../../dtos/gatewayDto';

@Injectable({ providedIn: 'root' })
export class ProcessesService {
  private http = inject(HttpClient);
  private base = '/api/processes';

  list(params?: { q?: string; status?: string }): Observable<ProcessDTO[]> {
    let p = new HttpParams();
    if (params?.q) p = p.set('q', params.q);
    if (params?.status) p = p.set('status', params.status);
    return this.http.get<ProcessDTO[]>(this.base, { params: p });
  }

  getById(id: string): Observable<ProcessDTO> {
    return this.http.get<ProcessDTO>(`${this.base}/${id}`);
  }

  create(body: Partial<ProcessDTO>): Observable<ProcessDTO> {
    return this.http.post<ProcessDTO>(this.base, body);
  }

  update(id: string, body: Partial<ProcessDTO>): Observable<ProcessDTO> {
    return this.http.put<ProcessDTO>(`${this.base}/${id}`, body);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  // Tabs
  listActivities(processId: string) { return this.http.get<ActivityDTO[]>(`${this.base}/${processId}/activities`); }
  listEdges(processId: string)      { return this.http.get<EdgeDTO[]>(`${this.base}/${processId}/edges`); }
  listGateways(processId: string)   { return this.http.get<GatewayDTO[]>(`${this.base}/${processId}/gateways`); }
}
