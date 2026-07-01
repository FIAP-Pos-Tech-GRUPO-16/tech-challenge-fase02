ALTER TABLE users ADD COLUMN IF NOT EXISTS tipo_usuario_id UUID;

ALTER TABLE users ADD CONSTRAINT fk_users_tipo_usuario
    FOREIGN KEY (tipo_usuario_id) REFERENCES tipos_usuario(id);
