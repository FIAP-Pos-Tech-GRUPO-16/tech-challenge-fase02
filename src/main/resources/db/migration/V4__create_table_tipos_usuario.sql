CREATE TABLE IF NOT EXISTS tipos_usuario (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_tipos_usuario_nome ON tipos_usuario(nome);
