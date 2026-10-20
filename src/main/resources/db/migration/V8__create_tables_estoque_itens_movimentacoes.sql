-- V8__create_tables_estoque_itens_movimentacoes.sql
-- Adiciona controle de estoque mínimo à tabela peca_insumo
-- e cria tabela de movimentações vinculada a peca_insumo.
-- Autor: Nicole
-- Data: 2026-06-09
-- Contexto Delimitado: estoque

-- ============================================
-- 1. ADICIONAR COLUNA DE QUANTIDADE MÍNIMA
-- ============================================
ALTER TABLE peca_insumo
    ADD COLUMN IF NOT EXISTS quantidade_minima INTEGER NOT NULL DEFAULT 0
        CONSTRAINT ck_peca_quantidade_minima CHECK (quantidade_minima >= 0);

COMMENT ON COLUMN peca_insumo.quantidade_minima IS 'Quantidade mínima desejada em estoque (dispara alerta quando abaixo)';

-- ============================================
-- 2. CRIAR TABELA DE MOVIMENTAÇÕES DE ESTOQUE
-- ============================================
CREATE TABLE IF NOT EXISTS movimentacoes_estoque (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    peca_insumo_id      UUID         NOT NULL,
    tipo_movimentacao   VARCHAR(20)  NOT NULL,
    quantidade          INTEGER      NOT NULL,
    observacao          VARCHAR(500),
    criado_em           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_movimentacao_peca_insumo FOREIGN KEY (peca_insumo_id) REFERENCES peca_insumo(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT ck_movimentacao_tipo  CHECK (tipo_movimentacao IN ('ENTRADA', 'SAIDA', 'AJUSTE')),
    CONSTRAINT ck_movimentacao_qtd   CHECK (quantidade > 0)
);

-- ============================================
-- 3. CRIAR ÍNDICES PARA OTIMIZAÇÃO
-- ============================================
CREATE INDEX IF NOT EXISTS idx_movimentacao_peca_insumo ON movimentacoes_estoque(peca_insumo_id);
CREATE INDEX IF NOT EXISTS idx_movimentacao_tipo ON movimentacoes_estoque(tipo_movimentacao);
CREATE INDEX IF NOT EXISTS idx_movimentacao_criado_em ON movimentacoes_estoque(criado_em);

-- ============================================
-- 4. ADICIONAR COMENTÁRIOS
-- ============================================
COMMENT ON TABLE  movimentacoes_estoque IS 'Histórico de movimentações de estoque (entrada/saída) - Contexto: estoque';
COMMENT ON COLUMN movimentacoes_estoque.id IS 'Identificador único da movimentação (UUID)';
COMMENT ON COLUMN movimentacoes_estoque.peca_insumo_id IS 'Referência à peça ou insumo movimentado (chave estrangeira)';
COMMENT ON COLUMN movimentacoes_estoque.tipo_movimentacao IS 'Tipo de movimentação: ENTRADA (compra), SAIDA (uso), AJUSTE (correção de estoque)';
COMMENT ON COLUMN movimentacoes_estoque.quantidade IS 'Quantidade movimentada (sempre positiva, sinal indicado pelo tipo)';
COMMENT ON COLUMN movimentacoes_estoque.observacao IS 'Observação ou justificativa da movimentação';
COMMENT ON COLUMN movimentacoes_estoque.criado_em IS 'Data e hora do registro da movimentação';
