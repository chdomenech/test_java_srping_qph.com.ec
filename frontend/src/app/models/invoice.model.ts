export interface InvoiceItem {
  id?: number;
  productId: number;
  productName?: string;
  productCode?: string;
  quantity: number;
  unitPrice: number;
  taxRate: number;
  lineTotal: number;
}

export interface Invoice {
  id?: number;
  customerId: number;
  customerName?: string;
  providerId: number;
  providerName?: string;
  issueDate?: string;
  subtotal: number;
  taxTotal: number;
  total: number;
  status?: string;
  items: InvoiceItem[];
}




