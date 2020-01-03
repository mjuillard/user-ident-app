import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { finalize } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  constructor(private http: HttpClient) { }

  authenticate(email, password, callback) {

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json'
      }),
      observe: 'response' as 'body',
      responseType: 'json'
    };

    this.http.post('//localhost:8080/users/login', {email, password}, {observe: 'response'})
      .subscribe(response => {
            this.setSession(response);
            return callback && callback();
      });
  }

  private setSession(authResult) {

    const expiresAt = authResult.headers.get('expirationTime');
    const token = authResult.headers.get('Authorization');
    const userId = authResult.headers.get('userId');

    localStorage.setItem('id_token', token);
    localStorage.setItem('expires_at', expiresAt);
    localStorage.setItem('userId', userId);
  }

  isAuthenticated() {
    return new Date().getTime() < this.getExpiration().getTime();
  }

  private getExpiration() {
    const expiration = localStorage.getItem('expires_at');
    const expiresAt = Number(expiration);
    return new Date(expiresAt);
  }

  getUserId() {
    return localStorage.getItem('userId');
  }

  logout(router) {
    localStorage.removeItem('id_token');
    localStorage.removeItem('expires_at');
    localStorage.removeItem('userId');

    this.http.post('//localhost:8080/logout', {}).pipe( finalize(() => {
        router.navigateByUrl('//localhost:8080/users/login');
    })).subscribe();
  }
}
