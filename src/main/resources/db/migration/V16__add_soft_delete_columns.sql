-- V16__add_soft_delete_columns.sql
-- Adição de colunas deleted_at para implementar Soft Delete
-- Isto permite deletar registros mantendo o histórico
-- Autor: Claude Code
-- Data: 2026-06-28

-- Adicionar coluna deleted_at na tabela veiculos
ALTER TABLE veiculos
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL;

-- Adicionar coluna deleted_at na tabela peca_insumo
ALTER TABLE peca_insumo
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL;

-- Criar índices para otimizar consultas com filtro de deleted_at
CREATE INDEX IF NOT EXISTS idx_veiculos_deleted_at ON veiculos(deleted_at);
CREATE INDEX IF NOT EXISTS idx_peca_insumo_deleted_at ON peca_insumo(deleted_at);

-- Comentários nas colunas
COMMENT ON COLUMN veiculos.deleted_at IS 'Data/hora da exclusão lógica do veículo. NULL = ativo, data = deletado';
COMMENT ON COLUMN peca_insumo.deleted_at IS 'Data/hora da exclusão lógica do item. NULL = ativo, data = deletado';
