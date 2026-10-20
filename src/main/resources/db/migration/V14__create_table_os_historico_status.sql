-- V14__create_table_os_historico_status.sql
-- Cria a tabela de histórico de movimentação de status das ordens de serviço
-- Contexto: atendimento

CREATE TABLE IF NOT EXISTS os_historico_status (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ordem_servico_id BIGINT NOT NULL,
    status_origem VARCHAR(30) NULL,
    status_destino VARCHAR(30) NOT NULL,
    data_mudanca TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id UUID NULL,

    CONSTRAINT fk_os_historico_status_ordem_servico FOREIGN KEY (ordem_servico_id)
        REFERENCES ordens_servico(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_os_historico_status_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT ck_os_historico_status_origem CHECK (
        status_origem IS NULL OR status_origem IN (
            'RECEBIDA',
            'EM_DIAGNOSTICO',
            'AGUARDANDO_APROVACAO',
            'EM_EXECUCAO',
            'FINALIZADA',
            'ENTREGUE'
        )
    ),
    CONSTRAINT ck_os_historico_status_destino CHECK (
        status_destino IN (
            'RECEBIDA',
            'EM_DIAGNOSTICO',
            'AGUARDANDO_APROVACAO',
            'EM_EXECUCAO',
            'FINALIZADA',
            'ENTREGUE'
        )
    )
);

CREATE INDEX IF NOT EXISTS idx_os_historico_status_ordem_servico_id
    ON os_historico_status(ordem_servico_id);

COMMENT ON TABLE os_historico_status IS 'Trilha de auditoria das mudanças de status das ordens de serviço';
COMMENT ON COLUMN os_historico_status.status_origem IS 'Status anterior da OS; pode ser nulo na primeira transição registrada';
COMMENT ON COLUMN os_historico_status.status_destino IS 'Novo status atribuído à OS';
COMMENT ON COLUMN os_historico_status.data_mudanca IS 'Data e hora em que a mudança de status foi registrada';
COMMENT ON COLUMN os_historico_status.usuario_id IS 'Usuário responsável pela alteração de status, quando identificado';

