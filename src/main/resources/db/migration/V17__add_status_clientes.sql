-- V17__add_status_clientes.sql
-- Adiciona coluna status à tabela clientes, usada pela autenticação via CPF
-- (Function Serverless) para validar se o cliente está apto a autenticar.
-- Contexto Delimitado: cadastro

ALTER TABLE clientes
    ADD COLUMN IF NOT EXISTS status VARCHAR(10) NOT NULL DEFAULT 'ATIVO'
        CONSTRAINT ck_cliente_status CHECK (status IN ('ATIVO', 'INATIVO'));

CREATE INDEX IF NOT EXISTS idx_cliente_status ON clientes(status);

COMMENT ON COLUMN clientes.status IS 'Status do cliente: ATIVO (pode autenticar via CPF) ou INATIVO (bloqueado)';
