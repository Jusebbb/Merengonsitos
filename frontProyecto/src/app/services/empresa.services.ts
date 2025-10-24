import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { EmpresaDTO } from '../models/empresa.dto';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';


@Injectable({ providedIn: 'root' })
export class EmpresaService {
  private base = `${environment.apiBase}/empresas`;

  constructor(private http: HttpClient) {}

  crear(dto: Omit<EmpresaDTO, 'id'>): Observable<EmpresaDTO> {
    return this.http.post<EmpresaDTO>(this.base, dto);
  }
}
