-- Script de migración para agregar roles al sistema
-- Fecha: 2025-01-XX

-- 1. Agregar columna 'role' a la tabla users
ALTER TABLE users 
ADD COLUMN role VARCHAR(20);

-- 2. Asignar ROLE_USER a todos los usuarios existentes
UPDATE users 
SET role = 'ROLE_USER' 
WHERE role IS NULL;

-- 3. Hacer la columna NOT NULL
ALTER TABLE users 
MODIFY COLUMN role VARCHAR(20) NOT NULL;

-- 4. Crear un usuario administrador de prueba
-- Email: admin@test.com, Contraseña: admin123 (ya hasheada con BCrypt)
INSERT INTO users (email, password, role) 
VALUES ('admin@test.com', '$2a$10$N9qo8uLOickgx2ZqVzNz8eUK/Z8S8JhF9BfmJ2xY3GxZ4K8wZ4K8S', 'ROLE_ADMIN')
ON DUPLICATE KEY UPDATE role = 'ROLE_ADMIN';

-- 5. Promover el usuario de prueba existente a ADMIN
UPDATE users 
SET role = 'ROLE_ADMIN' 
WHERE email = 'prueba@test.com';

-- 6. Verificar los cambios
SELECT id, email, role FROM users;