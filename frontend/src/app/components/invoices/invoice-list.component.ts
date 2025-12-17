import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { Invoice } from '../../models/invoice.model';

@Component({
  selector: 'app-invoice-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './invoice-list.component.html'
})
export class InvoiceListComponent implements OnInit {
  invoices: Invoice[] = [];
  page = 0;
  size = 10;
  totalPages = 1;
  search = '';

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadInvoices();
  }

  loadInvoices() {
    this.apiService.getInvoices(this.page, this.size, this.search)
      .subscribe({
        next: (response: any) => {
          this.invoices = response.content;
          this.totalPages = response.totalPages;
        },
        error: (err) => {
          console.error('Error loading invoices', err);
        }
      });
  }

  downloadPDF(id: number) {
    this.apiService.downloadInvoicePDF(id)
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `invoice_${id}.pdf`;
          a.click();
          window.URL.revokeObjectURL(url);
        },
        error: (err) => {
          console.error('Error downloading PDF', err);
          alert('Error downloading PDF');
        }
      });
  }

  deleteInvoice(id: number) {
    if (confirm('Are you sure you want to delete this invoice?')) {
      this.apiService.deleteInvoice(id)
        .subscribe({
          next: () => this.loadInvoices(),
          error: (err) => {
            console.error('Error deleting invoice', err);
            alert('Error deleting invoice');
          }
        });
    }
  }

  previousPage() {
    if (this.page > 0) {
      this.page--;
      this.loadInvoices();
    }
  }

  nextPage() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadInvoices();
    }
  }
}




