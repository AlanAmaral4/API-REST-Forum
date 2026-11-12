ALTER TABLE usuario ADD COLUMN senha VARCHAR(100);
-- Senha abaixo é "123456" criptografada com BCrypt
UPDATE usuario SET senha = '$2a$10$vDrJIwhfD3gBgrIP0s2uwueYfLnZpnRJJY4tr5L8QgJkxe5Cz5hm6' WHERE id = 1;
UPDATE usuario SET senha = '$2a$10$vDrJIwhfD3gBgrIP0s2uwueYfLnZpnRJJY4tr5L8QgJkxe5Cz5hm6' WHERE id = 2;
UPDATE usuario SET senha = '$2a$10$vDrJIwhfD3gBgrIP0s2uwueYfLnZpnRJJY4tr5L8QgJkxe5Cz5hm6' WHERE id = 3;