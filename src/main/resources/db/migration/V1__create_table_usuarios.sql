-- V1__create_table_usuarios.sql
-- Criação da tabela de usuários para o contexto de acesso
-- Autor: Nicole
-- Data: 2026-06-08

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    perfil VARCHAR(20) NOT NULL,
    cpf_cnpj VARCHAR(18) NOT NULL,

    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT uk_usuario_cpf_cnpj UNIQUE (cpf_cnpj),
    CONSTRAINT ck_usuario_perfil CHECK (perfil IN ('ADMIN', 'FUNCIONARIO', 'CLIENTE'))
);

-- Índices para otimização de consultas
CREATE INDEX IF NOT EXISTS idx_usuario_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuario_cpf_cnpj ON usuarios(cpf_cnpj);
CREATE INDEX IF NOT EXISTS idx_usuario_perfil ON usuarios(perfil);

-- Comentários nas colunas para documentação
COMMENT ON TABLE usuarios IS 'Tabela de usuários do sistema - Contexto: acesso';
COMMENT ON COLUMN usuarios.id IS 'Identificador único do usuário (UUID)';
COMMENT ON COLUMN usuarios.nome IS 'Nome completo do usuário';
COMMENT ON COLUMN usuarios.email IS 'Email único do usuário para login';
COMMENT ON COLUMN usuarios.senha IS 'Hash BCrypt da senha (nunca armazenar senha em texto puro)';
COMMENT ON COLUMN usuarios.telefone IS 'Telefone de contato do usuário';
COMMENT ON COLUMN usuarios.perfil IS 'Perfil de acesso: ADMIN, FUNCIONARIO ou CLIENTE';
COMMENT ON COLUMN usuarios.cpf_cnpj IS 'CPF ou CNPJ único do usuário';

