import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {
  user: any = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.user = this.authService.getUser();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}




