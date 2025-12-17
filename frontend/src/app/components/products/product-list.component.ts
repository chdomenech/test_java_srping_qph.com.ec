import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-list.component.html'
})
export class ProductListComponent implements OnInit {
  products: any[] = [];
  page = 0;
  size = 10;
  totalPages = 1;
  search = '';
  showModal = false;
  editingProduct: any = null;
  formProduct: any = { code: '', name: '', price: 0, taxRate: 0, stock: 0 };
  error = '';

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.apiService.getProducts(this.page, this.size, this.search)
      .subscribe({
        next: (response: any) => {
          this.products = response.content;
          this.totalPages = response.totalPages;
        },
        error: (err) => {
          this.error = err.error?.message || 'Error loading products';
        }
      });
  }

  openModal(product?: any) {
    this.editingProduct = product || null;
    this.formProduct = product ? { ...product } : { code: '', name: '', price: 0, taxRate: 0, stock: 0 };
    this.showModal = true;
    this.error = '';
  }

  closeModal() {
    this.showModal = false;
    this.editingProduct = null;
    this.error = '';
  }

  saveProduct() {
    if (this.editingProduct) {
      this.apiService.updateProduct(this.editingProduct.id!, this.formProduct)
        .subscribe({
          next: () => {
            this.closeModal();
            this.loadProducts();
          },
          error: (err) => {
            this.error = err.error?.message || 'Error updating product';
          }
        });
    } else {
      this.apiService.createProduct(this.formProduct)
        .subscribe({
          next: () => {
            this.closeModal();
            this.loadProducts();
          },
          error: (err) => {
            this.error = err.error?.message || 'Error creating product';
          }
        });
    }
  }

  editProduct(product: any) {
    this.openModal(product);
  }

  deleteProduct(id: number) {
    if (confirm('Are you sure you want to delete this product?')) {
      this.apiService.deleteProduct(id)
        .subscribe({
          next: () => this.loadProducts(),
          error: (err) => {
            this.error = err.error?.message || 'Error deleting product';
          }
        });
    }
  }

  previousPage() {
    if (this.page > 0) {
      this.page--;
      this.loadProducts();
    }
  }

  nextPage() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadProducts();
    }
  }
}




