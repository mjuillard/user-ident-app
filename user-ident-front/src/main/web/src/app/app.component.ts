import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppService } from './service/app.service';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Demo';
  greeting = {};

  constructor(private app: AppService, private http: HttpClient, private router: Router) {
  }

  logout() {

    this.app.logout();
  }

}
