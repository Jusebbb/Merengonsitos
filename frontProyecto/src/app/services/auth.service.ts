// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, RolUsuario } from '../dtos/loginDto';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // Ajusta a tu endpoint real:
  private API = '/api/auth';

  constructor(private http: HttpClient) {}

  login(body: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API}/login`, body).pipe(
      tap(res => {
        // Normalizamos el nombre del campo por si llega como "rol" o "role"
        const rawRole = (res.role ?? res.rol ?? '').toString().toUpperCase();
        const role = (['ADMIN', 'EDITOR', 'LECTOR'].includes(rawRole) ? rawRole : 'LECTOR') as RolUsuario;

        localStorage.setItem('token', res.token);
        localStorage.setItem('role', role);
        if (res.userId) localStorage.setItem('userId', res.userId);
        if (res.name) localStorage.setItem('name', res.name);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    localStorage.removeItem('name');
  }

  getRole(): RolUsuario | null {
    const v = localStorage.getItem('role');
    return (v === 'ADMIN' || v === 'EDITOR' || v === 'LECTOR') ? (v as RolUsuario) : null;
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }
}
