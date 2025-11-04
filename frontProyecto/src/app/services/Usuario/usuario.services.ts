import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UsuarioDTO } from '../../dtos/usuario.dto';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private urlServidor:string= "http://localhost:8080/api";
  private urlCrear = this.urlServidor+"/empresas";


  constructor(private http: HttpClient) { }

  createUser(usuarioDto: UsuarioDTO ): Observable<Object> {
  return this.http.post(this.urlCrear, usuarioDto);
}

}
