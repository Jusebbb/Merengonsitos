import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, tap, map } from 'rxjs';

export interface LoginRequest { email: string; password: string; }
export interface LoginResponse { token: string; role?: string; userId?: string; name?: string; empresaId?: number; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private API = `${environment.apiBase}/auth`;
  constructor(private http: HttpClient) {
    console.log('[AuthService] API =', this.API);
  }

  login(body: LoginRequest): Observable<void> {
    console.log('[AuthService] POST', `${this.API}/login`, body);
    return this.http.post<LoginResponse>(`${this.API}/login`, body).pipe(
      tap(res => {
        console.log('[AuthService] response', res);
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', (res.role ?? '').toUpperCase());
        localStorage.setItem('userId', res.userId ?? '');
        localStorage.setItem('name', res.name ?? '');
        if (res.empresaId != null) localStorage.setItem('empresaId', String(res.empresaId));
      }),
      map(() => void 0)
    );
  }
}
