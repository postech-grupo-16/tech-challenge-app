-- V3__create_table_veiculos.sql
-- Criação da tabela de veículos para o contexto de cadastro
-- Autor: Nicole
-- Data: 2026-06-08

CREATE TABLE IF NOT EXISTS veiculos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    placa VARCHAR(8) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    ano INTEGER NOT NULL,
    cor VARCHAR(50) NOT NULL,

    CONSTRAINT uk_veiculo_placa UNIQUE (placa)
);

-- Índices para otimização de consultas
CREATE INDEX IF NOT EXISTS idx_veiculo_placa ON veiculos(placa);
CREATE INDEX IF NOT EXISTS idx_veiculo_marca ON veiculos(marca);
CREATE INDEX IF NOT EXISTS idx_veiculo_modelo ON veiculos(modelo);

-- Comentários nas colunas para documentação
COMMENT ON TABLE veiculos IS 'Tabela de veículos cadastrados - Contexto: cadastro';
COMMENT ON COLUMN veiculos.id IS 'Identificador único do veículo (UUID)';
COMMENT ON COLUMN veiculos.placa IS 'Placa única do veículo (formato Mercosul ou antigo)';
COMMENT ON COLUMN veiculos.marca IS 'Marca/fabricante do veículo (Toyota, Honda, Chevrolet, etc)';
COMMENT ON COLUMN veiculos.modelo IS 'Modelo do veículo (Corolla, Civic, Onix, etc)';
COMMENT ON COLUMN veiculos.ano IS 'Ano de fabricação do veículo';
COMMENT ON COLUMN veiculos.cor IS 'Cor do veículo';

