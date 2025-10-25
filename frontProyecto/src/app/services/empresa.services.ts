import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EmpresaDTO } from '../dtos/empresa.dto';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EmpresaService {
  private base = `${environment.apiBase}/empresas`; // /api en dev (proxy)

  constructor(private http: HttpClient) {}

  crear(dto: Omit<EmpresaDTO, 'id'>) {
  return this.http.post<EmpresaDTO>(this.base, dto, { observe: 'response' });
}
}
