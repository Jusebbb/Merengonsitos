import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  token?: string;
  rol?: string;    
  role?: string;  
  user?: { rol?: string; role?: string };
  usuario?: { rol?: string; role?: string };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = '/api/auth'; // ajusta si tu backend usa otra ruta

  constructor(private http: HttpClient) {}

  login(body: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.api}/login`, body).pipe(
      tap(res => {
        const token = res.token ?? '';
        const role =
          (res.rol || res.role ||
           res.user?.rol || res.user?.role ||
           res.usuario?.rol || res.usuario?.role || ''
          ).toString().toUpperCase();

        if (token) localStorage.setItem('token', token);
        if (role)  localStorage.setItem('rol', role);
      })
    );
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('rol');
  }

  getToken(): string | null { return localStorage.getItem('token'); }
  getRole(): string | null { return localStorage.getItem('rol'); }
  isLoggedIn(): boolean { return !!this.getToken(); }
}
