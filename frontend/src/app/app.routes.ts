import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { CustomerListComponent } from './components/customers/customer-list.component';
import { ProviderListComponent } from './components/providers/provider-list.component';
import { ProductListComponent } from './components/products/product-list.component';
import { InvoiceListComponent } from './components/invoices/invoice-list.component';
import { InvoiceFormComponent } from './components/invoices/invoice-form.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'customers', component: CustomerListComponent, canActivate: [authGuard] },
  { path: 'providers', component: ProviderListComponent, canActivate: [authGuard] },
  { path: 'products', component: ProductListComponent, canActivate: [authGuard] },
  { path: 'invoices', component: InvoiceListComponent, canActivate: [authGuard] },
  { path: 'invoices/new', component: InvoiceFormComponent, canActivate: [authGuard] },
  { path: '', redirectTo: '/invoices', pathMatch: 'full' },
  { path: '**', redirectTo: '/invoices' }
];




