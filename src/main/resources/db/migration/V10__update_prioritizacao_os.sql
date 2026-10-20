-- V10__update_prioritizacao_os.sql
-- Atualiza a priorização de ordens de serviço com categoria de serviço e flag de urgência
-- Contexto: atendimento

-- =====================================================
-- ALTERAÇÃO: servico_catalogo
-- =====================================================
ALTER TABLE servico_catalogo
    ADD COLUMN IF NOT EXISTS categoria VARCHAR(30);

UPDATE servico_catalogo
SET categoria = 'PREVENTIVA'
WHERE categoria IS NULL;

ALTER TABLE servico_catalogo
    ALTER COLUMN categoria SET DEFAULT 'PREVENTIVA',
    ALTER COLUMN categoria SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'ck_servico_catalogo_categoria'
    ) THEN
        ALTER TABLE servico_catalogo
            ADD CONSTRAINT ck_servico_catalogo_categoria
            CHECK (categoria IN ('DIAGNOSTICO', 'CORRETIVA', 'PREVENTIVA', 'GARANTIA'));
    END IF;
END $$;

COMMENT ON COLUMN servico_catalogo.categoria IS
    'Categoria de priorização do serviço para apoio à ordenação operacional da oficina';

-- =====================================================
-- ALTERAÇÃO: ordens_servico
-- =====================================================
ALTER TABLE ordens_servico
    ADD COLUMN IF NOT EXISTS urgente BOOLEAN;

UPDATE ordens_servico
SET urgente = FALSE
WHERE urgente IS NULL;

ALTER TABLE ordens_servico
    ALTER COLUMN urgente SET DEFAULT FALSE,
    ALTER COLUMN urgente SET NOT NULL;

COMMENT ON COLUMN ordens_servico.urgente IS
    'Flag de prioridade máxima definida manualmente pela atendente por critérios humanos ou comerciais';

CREATE INDEX IF NOT EXISTS idx_ordens_servico_urgente
    ON ordens_servico(urgente);

-- =====================================================
-- DML: atualização de dados fictícios da V8
-- =====================================================
UPDATE servico_catalogo
SET categoria = CASE id
    WHEN '77777777-7777-4777-8777-777777777771' THEN 'PREVENTIVA'
    WHEN '77777777-7777-4777-8777-777777777772' THEN 'PREVENTIVA'
    WHEN '77777777-7777-4777-8777-777777777773' THEN 'CORRETIVA'
    WHEN '77777777-7777-4777-8777-777777777774' THEN 'DIAGNOSTICO'
    ELSE categoria
END
WHERE id IN (
    '77777777-7777-4777-8777-777777777771',
    '77777777-7777-4777-8777-777777777772',
    '77777777-7777-4777-8777-777777777773',
    '77777777-7777-4777-8777-777777777774'
);

UPDATE ordens_servico
SET urgente = TRUE
WHERE id = 20260001;

-- =====================================================
-- BÔNUS: consulta de validação para o painel administrativo
-- =====================================================
-- SELECT id, cliente_id, veiculo_id, status, urgente, data_criacao
-- FROM ordens_servico
-- ORDER BY urgente DESC, data_criacao ASC;

