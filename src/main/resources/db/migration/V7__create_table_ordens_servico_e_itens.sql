-- V7__create_table_ordens_servico_orcamentos_e_itens.sql
-- Cria as tabelas centrais do fluxo de OS com suporte a múltiplos orçamentos.
-- IDs sequenciais: Ordem de Serviço (YYYYNNNNN), Orçamento (YYNNNNNNN)
-- Autor: Nicole
-- Contexto: atendimento

-- Criar sequências para IDs sequenciais
CREATE SEQUENCE IF NOT EXISTS seq_ordem_servico_id START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS seq_orcamento_id START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS ordens_servico (
    id BIGINT PRIMARY KEY,
    cliente_id UUID NOT NULL,
    veiculo_id UUID NOT NULL,
    mecanico_id UUID NULL,
    status VARCHAR(30) NOT NULL,
    valor_total_acumulado NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    valor_total NUMERIC(10, 2) NULL DEFAULT 0.00,
    urgente BOOLEAN NOT NULL DEFAULT FALSE,
    queixa_cliente TEXT NOT NULL,
    observacoes TEXT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_inicio_execucao TIMESTAMP NULL,
    data_finalizacao TIMESTAMP NULL,

    CONSTRAINT fk_ordem_servico_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_ordem_servico_veiculo
        FOREIGN KEY (veiculo_id) REFERENCES veiculos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_ordem_servico_mecanico
        FOREIGN KEY (mecanico_id) REFERENCES usuarios(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT ck_ordem_servico_status CHECK (
        status IN ('RECEBIDA', 'EM_DIAGNOSTICO', 'AGUARDANDO_APROVACAO', 'EM_EXECUCAO', 'FINALIZADA', 'ENTREGUE')
    ),
    CONSTRAINT ck_ordem_servico_valor_total CHECK (valor_total_acumulado >= 0)
);

CREATE TABLE IF NOT EXISTS os_orcamentos (
    id BIGINT PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    valor_total NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    prazo_estipulado TIMESTAMP NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_orcamento_ordem_servico
        FOREIGN KEY (ordem_servico_id) REFERENCES ordens_servico(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT ck_orcamento_tipo CHECK (tipo IN ('INICIAL', 'ADICIONAL')),
    CONSTRAINT ck_orcamento_status CHECK (status IN ('PENDENTE', 'APROVADO', 'REJEITADO')),
    CONSTRAINT ck_orcamento_valor CHECK (valor_total >= 0)
);

CREATE TABLE IF NOT EXISTS os_servicos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    orcamento_id BIGINT NOT NULL,
    servico_id UUID NOT NULL,
    preco_mao_de_obra_aplicado NUMERIC(10, 2) NOT NULL,
    ordem_servico_id BIGINT NULL,

    CONSTRAINT fk_os_servico_orcamento
        FOREIGN KEY (orcamento_id) REFERENCES os_orcamentos(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_os_servico_servico_catalogo
        FOREIGN KEY (servico_id) REFERENCES servico_catalogo(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_os_servico_preco_aplicado CHECK (preco_mao_de_obra_aplicado >= 0)
);

CREATE TABLE IF NOT EXISTS os_pecas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    orcamento_id BIGINT NOT NULL,
    peca_id UUID NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_venda_aplicado NUMERIC(10, 2) NOT NULL,
    ordem_servico_id BIGINT NULL,

    CONSTRAINT fk_os_peca_orcamento
        FOREIGN KEY (orcamento_id) REFERENCES os_orcamentos(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_os_peca_peca_insumo
        FOREIGN KEY (peca_id) REFERENCES peca_insumo(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_os_peca_quantidade CHECK (quantidade > 0),
    CONSTRAINT ck_os_peca_preco_aplicado CHECK (preco_venda_aplicado >= 0)
);

-- Índices operacionais e de busca rápida
CREATE INDEX IF NOT EXISTS idx_ordens_servico_cliente_id ON ordens_servico(cliente_id);
CREATE INDEX IF NOT EXISTS idx_ordens_servico_status ON ordens_servico(status);
CREATE INDEX IF NOT EXISTS idx_os_orcamentos_os_id ON os_orcamentos(ordem_servico_id);
CREATE INDEX IF NOT EXISTS idx_os_servicos_orcamento_id ON os_servicos(orcamento_id);
CREATE INDEX IF NOT EXISTS idx_os_pecas_orcamento_id ON os_pecas(orcamento_id);

-- Índices adicionais recomendados para otimização de JOINS
CREATE INDEX IF NOT EXISTS idx_os_servicos_servico_id ON os_servicos(servico_id);
CREATE INDEX IF NOT EXISTS idx_os_pecas_peca_id ON os_pecas(peca_id);
