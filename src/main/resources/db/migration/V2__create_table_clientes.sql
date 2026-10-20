-- V2__create_table_clientes.sql
-- Criação da tabela de clientes para o contexto de cadastro
-- Autor: Nicole
-- Data: 2026-06-08

CREATE TABLE IF NOT EXISTS clientes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NULL,
    nome VARCHAR(150) NOT NULL,
    cpf_cnpj VARCHAR(18) NOT NULL,
    telefone VARCHAR(20),
    cep VARCHAR(10),
    rua VARCHAR(150),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    cidade VARCHAR(100),
    estado CHAR(2),

    CONSTRAINT uk_cliente_cpf_cnpj UNIQUE (cpf_cnpj),
    CONSTRAINT fk_cliente_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Índices para otimização de consultas
CREATE INDEX IF NOT EXISTS idx_cliente_cpf_cnpj ON clientes(cpf_cnpj);
CREATE INDEX IF NOT EXISTS idx_cliente_usuario_id ON clientes(usuario_id);
CREATE INDEX IF NOT EXISTS idx_cliente_nome ON clientes(nome);
CREATE INDEX IF NOT EXISTS idx_cliente_cidade_estado ON clientes(cidade, estado);

-- Comentários nas colunas para documentação
COMMENT ON TABLE clientes IS 'Tabela de clientes da oficina - Contexto: cadastro';
COMMENT ON COLUMN clientes.id IS 'Identificador único do cliente (UUID)';
COMMENT ON COLUMN clientes.usuario_id IS 'Referência opcional ao usuário do sistema';
COMMENT ON COLUMN clientes.nome IS 'Nome completo ou razão social do cliente';
COMMENT ON COLUMN clientes.cpf_cnpj IS 'CPF ou CNPJ único do cliente';
COMMENT ON COLUMN clientes.telefone IS 'Telefone de contato do cliente';
COMMENT ON COLUMN clientes.cep IS 'CEP do endereço do cliente';
COMMENT ON COLUMN clientes.rua IS 'Logradouro do endereço do cliente';
COMMENT ON COLUMN clientes.numero IS 'Número do endereço do cliente';
COMMENT ON COLUMN clientes.complemento IS 'Complemento do endereço do cliente';
COMMENT ON COLUMN clientes.cidade IS 'Cidade do cliente';
COMMENT ON COLUMN clientes.estado IS 'Sigla do estado (UF) do cliente';

