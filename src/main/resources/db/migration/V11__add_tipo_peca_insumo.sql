-- V11__add_tipo_peca_insumo.sql
-- Adiciona coluna tipo à tabela peca_insumo para diferenciar peças de insumos.
-- Contexto Delimitado: estoque

ALTER TABLE peca_insumo
    ADD COLUMN IF NOT EXISTS tipo VARCHAR(10) NOT NULL DEFAULT 'PECA'
        CONSTRAINT ck_peca_tipo CHECK (tipo IN ('PECA', 'INSUMO'));

COMMENT ON COLUMN peca_insumo.tipo IS 'Tipo do item: PECA (peça de reposição) ou INSUMO (consumível/material)';

-- Corrige o tipo dos itens cadastrados no seed inicial (V9)
UPDATE peca_insumo SET tipo = 'INSUMO' WHERE id = '66666666-6666-4666-8666-666666666661'; -- Oleo de Motor 5W30 Sintetico
UPDATE peca_insumo SET tipo = 'PECA'   WHERE id = '66666666-6666-4666-8666-666666666662'; -- Filtro de Oleo
UPDATE peca_insumo SET tipo = 'PECA'   WHERE id = '66666666-6666-4666-8666-666666666663'; -- Pastilha de Freio Dianteira
UPDATE peca_insumo SET tipo = 'PECA'   WHERE id = '66666666-6666-4666-8666-666666666664'; -- Disco de Freio Ventilado
UPDATE peca_insumo SET tipo = 'INSUMO' WHERE id = '66666666-6666-4666-8666-666666666665'; -- Fluido de Arrefecimento
