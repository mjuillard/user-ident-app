import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpXsrfTokenExtractor } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    intercept(req: HttpRequest<any>,
              next: HttpHandler): Observable<HttpEvent<any>> {

        const idToken = localStorage.getItem('id_token');

        // add CSRF token as HttpRequest
        const cloned = req.clone({
          headers: req.headers.set('X-Requested-With', 'XMLHttpRequest')
        });

        // add auth token
        if (idToken) {
          const authReq = cloned.clone({
              headers: req.headers.set('Authorization', idToken)
          });
          return next.handle(authReq);
        }

        return next.handle(cloned);
    }
}
