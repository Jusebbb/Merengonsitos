import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { EmpresaDTO } from '../../dtos/empresa.dto';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class EmpresaServices {
  
  private urlServidor: string = "http://localhost:8080/api";
  
  private urlCrear = this.urlServidor+"/empresas";

  constructor(private httpClient: HttpClient){}

  crearEmpresa(empresaDto: EmpresaDTO):Observable<Object>{
    return this.httpClient.post(`${this.urlCrear}`, empresaDto);
  }


}