export interface Customer {
  id?: number;
  name: string;
  docNumber: string;
  email: string;
  address?: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}




