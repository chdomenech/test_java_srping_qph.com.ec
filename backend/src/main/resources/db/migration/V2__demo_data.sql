-- Insert roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('USER') ON CONFLICT (name) DO NOTHING;

-- Insert demo users (password: Admin#123 and User#123 hashed with BCrypt)
INSERT INTO users (username, password_hash, enabled) VALUES
    ('admin@demo.com', '$2a$10$Q2.GYpOqLM7miX.52h2PMuOxtjL7b9XNKTX3NJ1k0EUH8onyMZE7u', TRUE),
    ('user@demo.com', '$2a$10$ThYkMdPTMP1MZO5OR9XKAudYh6cS0POQuHAgbHZMvpNTicmR8s6vu', TRUE)
ON CONFLICT (username) DO NOTHING;

-- Assign roles to users
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin@demo.com' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'user@demo.com' AND r.name = 'USER'
ON CONFLICT DO NOTHING;

-- Insert demo customers
INSERT INTO customers (name, doc_number, email, address) VALUES
    ('Juan Pérez', '12345678', 'juan.perez@example.com', 'Av. Principal 123, Lima'),
    ('María García', '87654321', 'maria.garcia@example.com', 'Calle Secundaria 456, Arequipa'),
    ('Carlos López', '11223344', 'carlos.lopez@example.com', 'Jr. Comercio 789, Trujillo')
ON CONFLICT (doc_number) DO NOTHING;

-- Insert demo providers
INSERT INTO providers (name, tax_id, email, address) VALUES
    ('Proveedor ABC S.A.', '20123456789', 'contacto@abc.com', 'Av. Industrial 100, Lima'),
    ('Distribuidora XYZ S.A.C.', '20198765432', 'ventas@xyz.com', 'Calle Mayor 200, Lima')
ON CONFLICT (tax_id) DO NOTHING;

-- Insert demo products
INSERT INTO products (code, name, price, tax_rate, stock) VALUES
    ('PROD001', 'Laptop Dell Inspiron', 2500.00, 18.00, 10),
    ('PROD002', 'Mouse Logitech MX Master', 89.99, 18.00, 25),
    ('PROD003', 'Teclado Mecánico RGB', 150.00, 18.00, 15),
    ('PROD004', 'Monitor LG 27 pulgadas', 350.00, 18.00, 8),
    ('PROD005', 'Auriculares Sony WH-1000XM4', 299.99, 18.00, 12)
ON CONFLICT (code) DO NOTHING;

