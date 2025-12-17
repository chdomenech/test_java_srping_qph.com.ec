import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    this.error = '';
    this.loading = true;

    this.authService.login({ username: this.username, password: this.password })
      .subscribe({
        next: () => {
          this.router.navigate(['/invoices']);
        },
        error: (err) => {
          this.error = err.error?.message || 'Invalid credentials';
          this.loading = false;
        }
      });
  }
}



