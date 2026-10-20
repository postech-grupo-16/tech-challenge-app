-- V4__create_table_cliente_veiculo.sql
-- Criação da tabela associativa cliente_veiculo (relacionamento N:M) para o contexto de cadastro
-- Permite que um cliente tenha vários veículos e mantém histórico caso ele venda o veículo
-- Autor: Nicole
-- Data: 2026-06-09

CREATE TABLE IF NOT EXISTS cliente_veiculo (
    cliente_id UUID NOT NULL,
    veiculo_id UUID NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT true,

    CONSTRAINT pk_cliente_veiculo PRIMARY KEY (cliente_id, veiculo_id),
    CONSTRAINT fk_cliente_veiculo_cliente FOREIGN KEY (cliente_id)
        REFERENCES clientes(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_cliente_veiculo_veiculo FOREIGN KEY (veiculo_id)
        REFERENCES veiculos(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Índices para otimização de consultas
CREATE INDEX IF NOT EXISTS idx_cliente_veiculo_cliente_id ON cliente_veiculo(cliente_id);
CREATE INDEX IF NOT EXISTS idx_cliente_veiculo_veiculo_id ON cliente_veiculo(veiculo_id);
CREATE INDEX IF NOT EXISTS idx_cliente_veiculo_ativo ON cliente_veiculo(ativo);
CREATE INDEX IF NOT EXISTS idx_cliente_veiculo_cliente_ativo ON cliente_veiculo(cliente_id, ativo);

-- Comentários nas colunas para documentação
COMMENT ON TABLE cliente_veiculo IS 'Tabela associativa de relacionamento N:M entre clientes e veículos - Contexto: cadastro';
COMMENT ON COLUMN cliente_veiculo.cliente_id IS 'Identificador do cliente (UUID) - Chave estrangeira para clientes';
COMMENT ON COLUMN cliente_veiculo.veiculo_id IS 'Identificador do veículo (UUID) - Chave estrangeira para veículos';
COMMENT ON COLUMN cliente_veiculo.ativo IS 'Indica se o veículo ainda pertence ao cliente (true = veículo ativo para o cliente, false = histórico)';


