import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CrearUsuarioRequest, UsuarioDTO } from '../dtos/usuario.dto';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private http = inject(HttpClient);
  // Con proxy: environment.apiBase = '/api'
  private base = `${environment.apiBase}/usuario`; // si tu backend usa plural, cambia a /usuarios

  crear(req: CrearUsuarioRequest): Observable<UsuarioDTO> {
    return this.http.post<UsuarioDTO>(this.base, req);
  }
}
