import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private tokenKey = 'auth_token';
  private userKey = 'user_data';

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request)
      .pipe(
        tap(response => {
          localStorage.setItem(this.tokenKey, response.token);
          localStorage.setItem(this.userKey, JSON.stringify({
            username: response.username,
            roles: response.roles
          }));
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getUser(): any {
    const userData = localStorage.getItem(this.userKey);
    return userData ? JSON.parse(userData) : null;
  }
}




