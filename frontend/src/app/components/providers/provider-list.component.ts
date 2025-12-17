import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-provider-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './provider-list.component.html'
})
export class ProviderListComponent implements OnInit {
  providers: any[] = [];
  page = 0;
  size = 10;
  totalPages = 1;
  search = '';
  showModal = false;
  editingProvider: any = null;
  formProvider: any = { name: '', taxId: '', email: '', address: '' };
  error = '';

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadProviders();
  }

  loadProviders() {
    this.apiService.getProviders(this.page, this.size, this.search)
      .subscribe({
        next: (response: any) => {
          this.providers = response.content;
          this.totalPages = response.totalPages;
        },
        error: (err) => {
          this.error = err.error?.message || 'Error loading providers';
        }
      });
  }

  openModal(provider?: any) {
    this.editingProvider = provider || null;
    this.formProvider = provider ? { ...provider } : { name: '', taxId: '', email: '', address: '' };
    this.showModal = true;
    this.error = '';
  }

  closeModal() {
    this.showModal = false;
    this.editingProvider = null;
    this.error = '';
  }

  saveProvider() {
    if (this.editingProvider) {
      this.apiService.updateProvider(this.editingProvider.id!, this.formProvider)
        .subscribe({
          next: () => {
            this.closeModal();
            this.loadProviders();
          },
          error: (err) => {
            this.error = err.error?.message || 'Error updating provider';
          }
        });
    } else {
      this.apiService.createProvider(this.formProvider)
        .subscribe({
          next: () => {
            this.closeModal();
            this.loadProviders();
          },
          error: (err) => {
            this.error = err.error?.message || 'Error creating provider';
          }
        });
    }
  }

  editProvider(provider: any) {
    this.openModal(provider);
  }

  deleteProvider(id: number) {
    if (confirm('Are you sure you want to delete this provider?')) {
      this.apiService.deleteProvider(id)
        .subscribe({
          next: () => this.loadProviders(),
          error: (err) => {
            this.error = err.error?.message || 'Error deleting provider';
          }
        });
    }
  }

  previousPage() {
    if (this.page > 0) {
      this.page--;
      this.loadProviders();
    }
  }

  nextPage() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadProviders();
    }
  }
}




