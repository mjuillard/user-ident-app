import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppService } from '../service/app.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  credentials = {email: '', password: ''};

  constructor(private app: AppService, private http: HttpClient, private router: Router) { }

  ngOnInit() {
  }

  login() {
    this.app.authenticate(this.credentials.email, this.credentials.password, () => {
        this.router.navigateByUrl('/');
    });
    return false;
  }

}
