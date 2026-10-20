-- V9__create_tables_estoque_itens_movimentacoes.sql
-- Adiciona controle de estoque mínimo à tabela peca_insumo
-- e cria tabela de movimentações vinculada a peca_insumo.
-- Contexto Delimitado: estoque

ALTER TABLE peca_insumo
    ADD COLUMN IF NOT EXISTS quantidade_minima INTEGER NOT NULL DEFAULT 0
        CONSTRAINT ck_peca_quantidade_minima CHECK (quantidade_minima >= 0);

COMMENT ON COLUMN peca_insumo.quantidade_minima IS 'Quantidade mínima desejada em estoque (dispara alerta quando abaixo)';


CREATE TABLE IF NOT EXISTS movimentacoes_estoque (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    peca_insumo_id      UUID         NOT NULL,
    tipo_movimentacao   VARCHAR(20)  NOT NULL,
    quantidade          INTEGER      NOT NULL,
    observacao          VARCHAR(500),
    criado_em           TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_movimentacao_peca_insumo FOREIGN KEY (peca_insumo_id) REFERENCES peca_insumo(id),
    CONSTRAINT ck_movimentacao_tipo  CHECK (tipo_movimentacao IN ('ENTRADA', 'SAIDA', 'VENDA', 'RESERVA', 'AJUSTE')),
    CONSTRAINT ck_movimentacao_qtd   CHECK (quantidade > 0)
);

CREATE INDEX IF NOT EXISTS idx_movimentacao_peca_insumo ON movimentacoes_estoque(peca_insumo_id);

COMMENT ON TABLE  movimentacoes_estoque IS 'Histórico de movimentações de estoque (entrada/saída) - Contexto: estoque';
