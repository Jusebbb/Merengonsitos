import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CrearUsuarioRequest, UsuarioDTO } from '../../dtos/usuario.dto';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private apiBase = `${environment.apiBase}/usuarios`;  // URL para crear usuarios

  constructor(private http: HttpClient) { }

  createUser(usuario: CrearUsuarioRequest): Observable<UsuarioDTO> {
  return this.http.post<UsuarioDTO>(this.apiBase, usuario);
}

}
