ALTER TABLE usuario_role RENAME COLUMN usuario_id TO id_usuario;
ALTER TABLE usuario_role RENAME COLUMN role_id TO id_role;

ALTER TABLE usuario_role ADD CONSTRAINT uk_usuario_role UNIQUE (id_usuario, id_role)