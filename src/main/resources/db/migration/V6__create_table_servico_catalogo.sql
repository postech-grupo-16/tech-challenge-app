-- V6__create_table_servico_catalogo.sql
-- Criação da tabela de catálogo de serviços para o contexto de atendimento
-- Define os serviços/mão de obra que a oficina oferece aos clientes
-- Autor: Nicole
-- Data: 2026-06-09

CREATE TABLE IF NOT EXISTS servico_catalogo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    preco_mao_de_obra NUMERIC(10, 2) NOT NULL,

    CONSTRAINT ck_servico_preco CHECK (preco_mao_de_obra > 0)
);

-- Índices para otimização de consultas
CREATE INDEX IF NOT EXISTS idx_servico_nome ON servico_catalogo(nome);
CREATE INDEX IF NOT EXISTS idx_servico_preco ON servico_catalogo(preco_mao_de_obra);

-- Comentários nas colunas para documentação
COMMENT ON TABLE servico_catalogo IS 'Tabela de catálogo de serviços disponíveis na oficina - Contexto: atendimento';
COMMENT ON COLUMN servico_catalogo.id IS 'Identificador único do serviço (UUID)';
COMMENT ON COLUMN servico_catalogo.nome IS 'Nome do serviço (Ex: Troca de Óleo, Alinhamento, Balanceamento)';
COMMENT ON COLUMN servico_catalogo.descricao IS 'Descrição detalhada do serviço oferecido';
COMMENT ON COLUMN servico_catalogo.preco_mao_de_obra IS 'Preço da mão de obra para o serviço (deve ser positivo)';


