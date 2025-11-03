import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, map } from 'rxjs';
import { LoginDto } from '../../dtos/loginDto';

export interface LoginRequest { email: string; password: string; }
export interface LoginResponse { token: string; role?: string; userId?: string; name?: string; empresaId?: number; }

@Injectable({ providedIn: 'root' })

export class AuthService {
  
  private urlServidor: string = "http://localhost:8080/api";

  private urlLogin = this.urlServidor+"/auth/login";
  
  constructor(private httpClient: HttpClient) {
    console.log('Ruta de login! =', this.urlLogin);
  }

  loginSolv(loginDto : LoginDto) : Observable<boolean>{
    return this.httpClient.post<boolean>(`${this.urlLogin}`, loginDto)
  }


}
