-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create users_roles junction table
CREATE TABLE IF NOT EXISTS users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    doc_number VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    address TEXT
);

-- Create providers table
CREATE TABLE IF NOT EXISTS providers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tax_id VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255),
    address TEXT
);

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    tax_rate NUMERIC(5, 2) NOT NULL,
    stock INTEGER NOT NULL
);

-- Create invoices table
CREATE TABLE IF NOT EXISTS invoices (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    issue_date TIMESTAMP NOT NULL,
    subtotal NUMERIC(19, 2) NOT NULL,
    tax_total NUMERIC(19, 2) NOT NULL,
    total NUMERIC(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (provider_id) REFERENCES providers(id)
);

-- Create invoice_items table
CREATE TABLE IF NOT EXISTS invoice_items (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(19, 2) NOT NULL,
    tax_rate NUMERIC(5, 2) NOT NULL,
    line_total NUMERIC(19, 2) NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_invoices_customer_id ON invoices(customer_id);
CREATE INDEX IF NOT EXISTS idx_invoices_provider_id ON invoices(provider_id);
CREATE INDEX IF NOT EXISTS idx_invoice_items_invoice_id ON invoice_items(invoice_id);
CREATE INDEX IF NOT EXISTS idx_invoice_items_product_id ON invoice_items(product_id);

