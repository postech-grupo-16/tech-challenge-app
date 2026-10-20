-- V15__insert_os_historico_status.sql
-- Inserção de histórico de status das ordens de serviço
-- Contexto: atendimento
-- Data: 2026-06-23

-- ============================================
-- HISTÓRICO DE STATUS (atendimento)
-- ============================================
INSERT INTO os_historico_status (id, ordem_servico_id, status_origem, status_destino, usuario_id, data_mudanca) VALUES
('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbb01', 20260001, NULL, 'RECEBIDA', NULL, CURRENT_TIMESTAMP - INTERVAL '1 day'),
('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbb02', 20260001, 'RECEBIDA', 'EM_DIAGNOSTICO', '77777777-7777-4777-8777-777777777771', CURRENT_TIMESTAMP - INTERVAL '1 day' + INTERVAL '1 hour'),
('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbb03', 20260002, NULL, 'RECEBIDA', NULL, CURRENT_TIMESTAMP - INTERVAL '12 hours'),
('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbb04', 20260002, 'RECEBIDA', 'EM_DIAGNOSTICO', '77777777-7777-4777-8777-777777777771', CURRENT_TIMESTAMP - INTERVAL '12 hours' + INTERVAL '1 hour'),
('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbb05', 20260002, 'EM_DIAGNOSTICO', 'AGUARDANDO_APROVACAO', '77777777-7777-4777-8777-777777777771', CURRENT_TIMESTAMP - INTERVAL '11 hours'),
('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbb06', 20260003, NULL, 'RECEBIDA', NULL, CURRENT_TIMESTAMP - INTERVAL '6 hours'),
('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbb07', 20260003, 'RECEBIDA', 'EM_DIAGNOSTICO', '77777777-7777-4777-8777-777777777771', CURRENT_TIMESTAMP - INTERVAL '6 hours' + INTERVAL '1 hour'),
('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbb08', 20260003, 'EM_DIAGNOSTICO', 'AGUARDANDO_APROVACAO', '77777777-7777-4777-8777-777777777771', CURRENT_TIMESTAMP - INTERVAL '5 hours'),
('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbb09', 20260003, 'AGUARDANDO_APROVACAO', 'EM_EXECUCAO', NULL, CURRENT_TIMESTAMP - INTERVAL '3 hours'),
('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbb10', 20260004, NULL, 'RECEBIDA', NULL, CURRENT_TIMESTAMP - INTERVAL '2 days'),
('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbb11', 20260004, 'RECEBIDA', 'EM_DIAGNOSTICO', '77777777-7777-4777-8777-777777777771', CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '1 hour')
ON CONFLICT DO NOTHING;
