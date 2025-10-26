import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest, LoginResponse, RolUsuario } from '../dtos/loginDto';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API = environment.apiBase; // ej: http://localhost:8080/api

  constructor(private http: HttpClient) {}

  login(body: LoginRequest): Observable<LoginResponse> {
    // POST http://localhost:8080/api/login
    return this.http.post<LoginResponse>(`${this.API}/login`, body).pipe(
      tap((res) => {
        const raw = (res.role ?? res.rol ?? '').toString().toUpperCase();
        const role: RolUsuario = raw === 'ADMIN' ? 'ADMIN' : (raw === 'EDITOR' ? 'EDITOR' : 'LECTOR');

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

  isLoggedIn(): boolean { return !!localStorage.getItem('token'); }
  getToken(): string | null { return localStorage.getItem('token'); }
}
