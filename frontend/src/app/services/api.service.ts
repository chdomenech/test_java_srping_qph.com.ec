import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer, PageResponse } from '../models/customer.model';
import { Invoice } from '../models/invoice.model';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Customers
  getCustomers(page: number = 0, size: number = 10, search?: string): Observable<PageResponse<Customer>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (search) {
      params = params.set('search', search);
    }
    return this.http.get<PageResponse<Customer>>(`${this.baseUrl}/customers`, { params });
  }

  getCustomer(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.baseUrl}/customers/${id}`);
  }

  createCustomer(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(`${this.baseUrl}/customers`, customer);
  }

  updateCustomer(id: number, customer: Customer): Observable<Customer> {
    return this.http.put<Customer>(`${this.baseUrl}/customers/${id}`, customer);
  }

  deleteCustomer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/customers/${id}`);
  }

  // Providers
  getProviders(page: number = 0, size: number = 10, search?: string): Observable<PageResponse<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (search) {
      params = params.set('search', search);
    }
    return this.http.get<PageResponse<any>>(`${this.baseUrl}/providers`, { params });
  }

  getProvider(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/providers/${id}`);
  }

  createProvider(provider: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/providers`, provider);
  }

  updateProvider(id: number, provider: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/providers/${id}`, provider);
  }

  deleteProvider(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/providers/${id}`);
  }

  // Products
  getProducts(page: number = 0, size: number = 10, search?: string): Observable<PageResponse<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (search) {
      params = params.set('search', search);
    }
    return this.http.get<PageResponse<any>>(`${this.baseUrl}/products`, { params });
  }

  getAvailableProducts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/products/available`);
  }

  getProduct(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/products/${id}`);
  }

  createProduct(product: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/products`, product);
  }

  updateProduct(id: number, product: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/products/${id}`, product);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/products/${id}`);
  }

  // Invoices
  getInvoices(page: number = 0, size: number = 10, search?: string): Observable<PageResponse<Invoice>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (search) {
      params = params.set('search', search);
    }
    return this.http.get<PageResponse<Invoice>>(`${this.baseUrl}/invoices`, { params });
  }

  getInvoice(id: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.baseUrl}/invoices/${id}`);
  }

  createInvoice(invoice: Invoice): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.baseUrl}/invoices`, invoice);
  }

  deleteInvoice(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/invoices/${id}`);
  }

  downloadInvoicePDF(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/invoices/${id}/report`, { responseType: 'blob' });
  }

  // AI
  getRecommendations(customerId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/ai/recommendations`, {
      params: { customerId: customerId.toString() }
    });
  }

  getAnomalyScore(invoiceId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/ai/anomaly-score`, {
      params: { invoiceId: invoiceId.toString() }
    });
  }
}




