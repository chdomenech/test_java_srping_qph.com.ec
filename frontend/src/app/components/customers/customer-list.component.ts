import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Customer, PageResponse } from '../../models/customer.model';

@Component({
  selector: 'app-customer-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customer-list.component.html'
})
export class CustomerListComponent implements OnInit {
  customers: Customer[] = [];
  page = 0;
  size = 10;
  totalPages = 1;
  search = '';
  showModal = false;
  editingCustomer: Customer | null = null;
  formCustomer: Customer = { name: '', docNumber: '', email: '', address: '' };
  error = '';

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadCustomers();
  }

  loadCustomers() {
    this.apiService.getCustomers(this.page, this.size, this.search)
      .subscribe({
        next: (response: PageResponse<Customer>) => {
          this.customers = response.content;
          this.totalPages = response.totalPages;
        },
        error: (err) => {
          this.error = err.error?.message || 'Error loading customers';
        }
      });
  }

  openModal(customer?: Customer) {
    this.editingCustomer = customer || null;
    this.formCustomer = customer ? { ...customer } : { name: '', docNumber: '', email: '', address: '' };
    this.showModal = true;
    this.error = '';
  }

  closeModal() {
    this.showModal = false;
    this.editingCustomer = null;
    this.error = '';
  }

  saveCustomer() {
    if (this.editingCustomer) {
      this.apiService.updateCustomer(this.editingCustomer.id!, this.formCustomer)
        .subscribe({
          next: () => {
            this.closeModal();
            this.loadCustomers();
          },
          error: (err) => {
            this.error = err.error?.message || 'Error updating customer';
          }
        });
    } else {
      this.apiService.createCustomer(this.formCustomer)
        .subscribe({
          next: () => {
            this.closeModal();
            this.loadCustomers();
          },
          error: (err) => {
            this.error = err.error?.message || 'Error creating customer';
          }
        });
    }
  }

  editCustomer(customer: Customer) {
    this.openModal(customer);
  }

  deleteCustomer(id: number) {
    if (confirm('Are you sure you want to delete this customer?')) {
      this.apiService.deleteCustomer(id)
        .subscribe({
          next: () => this.loadCustomers(),
          error: (err) => {
            this.error = err.error?.message || 'Error deleting customer';
          }
        });
    }
  }

  previousPage() {
    if (this.page > 0) {
      this.page--;
      this.loadCustomers();
    }
  }

  nextPage() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadCustomers();
    }
  }
}




