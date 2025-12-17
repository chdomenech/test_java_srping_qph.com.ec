import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { Invoice, InvoiceItem } from '../../models/invoice.model';

@Component({
  selector: 'app-invoice-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './invoice-form.component.html'
})
export class InvoiceFormComponent implements OnInit {
  invoice: Invoice = {
    customerId: 0,
    providerId: 0,
    subtotal: 0,
    taxTotal: 0,
    total: 0,
    items: []
  };
  
  customers: any[] = [];
  providers: any[] = [];
  availableProducts: any[] = [];
  recommendations: any = null;
  anomaly: any = null;
  error = '';

  constructor(
    private apiService: ApiService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadCustomers();
    this.loadProviders();
    this.loadAvailableProducts();
    this.addItem();
  }

  loadCustomers() {
    this.apiService.getCustomers(0, 100)
      .subscribe({
        next: (response: any) => {
          this.customers = response.content;
        },
        error: (err) => {
          this.error = 'Error loading customers';
        }
      });
  }

  loadProviders() {
    this.apiService.getProviders(0, 100)
      .subscribe({
        next: (response: any) => {
          this.providers = response.content;
        },
        error: (err) => {
          this.error = 'Error loading providers';
        }
      });
  }

  loadAvailableProducts() {
    this.apiService.getAvailableProducts()
      .subscribe({
        next: (products: any[]) => {
          this.availableProducts = products;
        },
        error: (err) => {
          this.error = 'Error loading products';
        }
      });
  }

  loadRecommendations() {
    if (this.invoice.customerId) {
      this.apiService.getRecommendations(this.invoice.customerId)
        .subscribe({
          next: (response: any) => {
            this.recommendations = response;
          },
          error: (err) => {
            console.error('Error loading recommendations', err);
          }
        });
    }
  }

  addRecommendedProduct(product: any) {
    const item: InvoiceItem = {
      productId: product.id,
      quantity: 1,
      unitPrice: product.price,
      taxRate: product.taxRate,
      lineTotal: 0
    };
    this.updateItemLineTotal(item);
    this.invoice.items.push(item);
    this.calculateTotals();
  }

  addItem() {
    this.invoice.items.push({
      productId: 0,
      quantity: 1,
      unitPrice: 0,
      taxRate: 0,
      lineTotal: 0
    });
  }

  removeItem(index: number) {
    this.invoice.items.splice(index, 1);
    this.calculateTotals();
  }

  updateItem(index: number) {
    const item = this.invoice.items[index];
    const product = this.availableProducts.find(p => p.id === item.productId);
    if (product) {
      item.unitPrice = product.price;
      item.taxRate = product.taxRate;
    }
    this.updateItemLineTotal(item);
    this.calculateTotals();
    this.checkAnomaly();
  }

  updateItemLineTotal(item: InvoiceItem) {
    const subtotal = item.unitPrice * item.quantity;
    const tax = subtotal * (item.taxRate / 100);
    item.lineTotal = subtotal + tax;
  }

  calculateTotals() {
    let subtotal = 0;
    let taxTotal = 0;
    
    this.invoice.items.forEach(item => {
      const lineSubtotal = item.unitPrice * item.quantity;
      const lineTax = lineSubtotal * (item.taxRate / 100);
      subtotal += lineSubtotal;
      taxTotal += lineTax;
    });
    
    this.invoice.subtotal = subtotal;
    this.invoice.taxTotal = taxTotal;
    this.invoice.total = subtotal + taxTotal;
  }

  checkAnomaly() {
  }

  saveInvoice() {
    this.error = '';
    this.apiService.createInvoice(this.invoice)
      .subscribe({
        next: (createdInvoice: Invoice) => {
          // Check anomaly after creation
          if (createdInvoice.id) {
            this.apiService.getAnomalyScore(createdInvoice.id)
              .subscribe({
                next: (anomalyResponse: any) => {
                  this.anomaly = anomalyResponse;
                  if (anomalyResponse.score > 0.5) {
                    if (!confirm('High anomaly detected. Do you want to proceed?')) {
                      return;
                    }
                  }
                  this.router.navigate(['/invoices']);
                },
                error: (err) => {
                  console.error('Error checking anomaly', err);
                  this.router.navigate(['/invoices']);
                }
              });
          } else {
            this.router.navigate(['/invoices']);
          }
        },
        error: (err) => {
          this.error = err.error?.message || 'Error creating invoice';
        }
      });
  }

  cancel() {
    this.router.navigate(['/invoices']);
  }
}




