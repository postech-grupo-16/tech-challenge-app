-- V5__create_table_peca_insumo.sql
-- Criação da tabela de peças e insumos para o contexto de estoque
-- Gerencia o inventário de peças e insumos utilizados nos serviços da oficina
-- Autor: Nicole
-- Data: 2026-06-09

CREATE TABLE IF NOT EXISTS peca_insumo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    preco_venda NUMERIC(10, 2) NOT NULL,
    preco_compra NUMERIC(10, 2) NOT NULL,
    quantidade_por_unidade VARCHAR(50),
    quantidade_estoque INTEGER NOT NULL,

    CONSTRAINT ck_peca_preco_venda CHECK (preco_venda > 0),
    CONSTRAINT ck_peca_preco_compra CHECK (preco_compra > 0),
    CONSTRAINT ck_peca_quantidade_estoque CHECK (quantidade_estoque >= 0),
    CONSTRAINT ck_peca_preco_compra_menor_venda CHECK (preco_compra <= preco_venda)
);

-- Índices para otimização de consultas
CREATE INDEX IF NOT EXISTS idx_peca_nome ON peca_insumo(nome);
CREATE INDEX IF NOT EXISTS idx_peca_quantidade_estoque ON peca_insumo(quantidade_estoque);

-- Comentários nas colunas para documentação
COMMENT ON TABLE peca_insumo IS 'Tabela de peças e insumos do estoque da oficina - Contexto: estoque';
COMMENT ON COLUMN peca_insumo.id IS 'Identificador único da peça/insumo (UUID)';
COMMENT ON COLUMN peca_insumo.nome IS 'Nome/descrição curta da peça ou insumo';
COMMENT ON COLUMN peca_insumo.descricao IS 'Descrição detalhada da peça ou insumo';
COMMENT ON COLUMN peca_insumo.preco_venda IS 'Preço unitário de venda da peça/insumo (deve ser positivo)';
COMMENT ON COLUMN peca_insumo.preco_compra IS 'Preço unitário de compra da peça/insumo (deve ser positivo)';
COMMENT ON COLUMN peca_insumo.quantidade_por_unidade IS 'Unidade de medida (Ex: Litro, Unidade, Jogo com 4, etc)';
COMMENT ON COLUMN peca_insumo.quantidade_estoque IS 'Quantidade disponível em estoque (não pode ser negativa)';


