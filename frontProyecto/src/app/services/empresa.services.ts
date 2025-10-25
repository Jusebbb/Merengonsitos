import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CrearEmpresaRequest, CrearEmpresaResponse } from '../dtos/registroDto';

@Injectable({ providedIn: 'root' })
export class EmpresaServices {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/empresas`; // => /api/empresas (lo resuelve el proxy)

  crearConAdmin(req: CrearEmpresaRequest): Observable<CrearEmpresaResponse> {
    const empresaPayload = {
      nombre: req.nombreEmpresa,
      nit: req.nit,
      correoContacto: req.correoEmpresa
    };
    return this.http.post<CrearEmpresaResponse>(this.base, empresaPayload);
  }
}
