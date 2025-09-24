import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const user = JSON.parse(localStorage.getItem('user') || 'null');
    const roles = route.data['roles'] as string[];

    if (user && roles.includes(user.role)) {
      return true;
    }

    this.router.navigate(['/login']);
    return false;
  }

}
