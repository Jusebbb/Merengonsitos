import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, tap, map } from 'rxjs';

export interface LoginRequest { email: string; password: string; }
export interface LoginResponse { token: string; role?: string; userId?: string; name?: string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private API = `${environment.apiBase}/auth`;
  constructor(private http: HttpClient) {}

  login(body: LoginRequest): Observable<void> {
    return this.http.post<LoginResponse>(`${this.API}/login`, body).pipe(
      tap(res => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', (res.role ?? '').toUpperCase());
        localStorage.setItem('userId', res.userId ?? '');
        localStorage.setItem('name', res.name ?? '');
      }),
      map(() => void 0)
    );
  }
}
