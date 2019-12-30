import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppService } from '../service/app.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  user;

  constructor(private app: AppService, private http: HttpClient) { }

  ngOnInit() {
    if (!this.user) {
      this.http.get('http://localhost:8080/users/' + this.app.getUserId())
              .subscribe( data => this.user = data);
    }
  }

  isLoggedIn() {
    return this.app.isAuthenticated();
  }
}
