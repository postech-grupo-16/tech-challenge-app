-- V13__seed_initial_data_oficina.sql
-- Dados iniciais de teste para a oficina mecânica
-- Estrutura conforme definido em V1-V7 (sem campos criado_em/criado_at nas tabelas base)
-- CPF/CNPJ sem formatação conforme requerido
-- Autor: Nicole
-- Data: 2026-06-09
-- Contexto Delimitado: acesso, cadastro, atendimento, estoque

-- ============================================
-- 1. USUÁRIOS (acesso)
-- ============================================
INSERT INTO usuarios (id, nome, email, cpf_cnpj, perfil, senha, telefone) VALUES
('77777777-7777-4777-8777-777777777770', 'Carlos Alberto Souza', 'admin@oficina.com', '12345678901234', 'ADMIN', '$2y$10$mFqh8hTdGLkdvZaJp7Fb1uUm7RrWSFgoWVgPMjJPzd8gP.3snTNw2', '11988881000'),
('77777777-7777-4777-8777-777777777771', 'Marcos Vinícius Lima', 'mecanico@oficina.com', '98765432109876', 'FUNCIONARIO', '$2y$10$uk/Qc20ovY0hACjdEhECweO1wkqhAHqMER..w7VV9VN3xHi2alEja', '11988882001'),
('77777777-7777-4777-8777-777777777772', 'Ana Paula Ribeiro', 'ana.ribeiro@cliente.local', '52998224725', 'CLIENTE', '$2y$10$ZBA7G5klVUBsfUJwwjd8R.Bgr9NQrZ2Eb8pixmU9ccFwPy47U9qKm', '11977773001'),
('550e8400-e29b-41d4-a716-446655440001', 'Nicole M Afonso', 'nicolemattesafonso06@gmail.com', '10387398790', 'CLIENTE', '$2a$10$H5lVQmKaHmQ4sPCN7.0p.eHGWlLvs7G.Q3R6K7N9M2E5L3Q2D0X0O', '11987654321')
ON CONFLICT DO NOTHING;

-- ============================================
-- 2. CLIENTES (cadastro)
-- ============================================
INSERT INTO clientes (id, usuario_id, nome, cpf_cnpj, telefone, cep, rua, numero, complemento, cidade, estado) VALUES
('88888888-8888-4888-8888-888888888880', '77777777-7777-4777-8777-777777777772', 'Ana Paula Ribeiro', '52998224725', '11977773001', '01310100', 'Avenida Paulista', '1578', 'Apto 92', 'Sao Paulo', 'SP'),
('88888888-8888-4888-8888-888888888881', NULL, 'Ricardo Mendes Costa', '11144477735', '11977773002', '04003001', 'Rua Domingos de Morais', '2450', NULL, 'Sao Paulo', 'SP'),
('88888888-8888-4888-8888-888888888882', NULL, 'Patricia Gomes Nunes', '39053344705', '11966664003', '09750530', 'Rua das Acacias', '312', 'Casa 2', 'Sao Bernardo do Campo', 'SP'),
('550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'Nicole M Afonso', '10387398790', '11987654321', '87615623', 'Rua das Flores', '123', 'Casa', 'Sao Bernardo do Campo', 'SP')
ON CONFLICT DO NOTHING;

-- ============================================
-- 3. VEÍCULOS (cadastro)
-- ============================================
INSERT INTO veiculos (id, placa, marca, modelo, ano, cor) VALUES
('66666666-6666-4666-8666-666666666660', 'ABC1234', 'Toyota', 'Corolla', 2020, 'Prata'),
('66666666-6666-4666-8666-666666666661', 'XYZ9876', 'Honda', 'Civic', 2021, 'Preto'),
('66666666-6666-4666-8666-666666666662', 'QWE1A34', 'Chevrolet', 'Onix', 2022, 'Branco'),
('550e8400-e29b-41d4-a716-446655440003', 'ABD1D28', 'Ford', 'Ka', 2000, 'Azul')
ON CONFLICT DO NOTHING;

-- ============================================
-- 4. CLIENTE_VEICULO (relacionamento N:M)
-- ============================================
INSERT INTO cliente_veiculo (cliente_id, veiculo_id, ativo) VALUES
('88888888-8888-4888-8888-888888888880', '66666666-6666-4666-8666-666666666660', TRUE),
('88888888-8888-4888-8888-888888888881', '66666666-6666-4666-8666-666666666661', TRUE),
('88888888-8888-4888-8888-888888888882', '66666666-6666-4666-8666-666666666662', TRUE),
('550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440003', TRUE)
ON CONFLICT DO NOTHING;

-- ============================================
-- 5. CATÁLOGO DE SERVIÇOS (atendimento)
-- ============================================
INSERT INTO servico_catalogo (id, nome, descricao, preco_mao_de_obra) VALUES
('77777777-7777-4777-8777-777777777773', 'Troca de Óleo e Filtro', 'Troca de óleo do motor, filtro de óleo e reset de indicador de manutenção', 120.00),
('77777777-7777-4777-8777-777777777774', 'Alinhamento e Balanceamento', 'Alinhamento computadorizado e balanceamento das quatro rodas', 180.00),
('77777777-7777-4777-8777-777777777775', 'Troca de Pastilhas de Freio', 'Substituição de pastilhas dianteiras com limpeza e lubrificação de componentes', 160.00),
('77777777-7777-4777-8777-777777777776', 'Diagnóstico Computadorizado', 'Leitura de falhas, análise de parâmetros e emissão de relatório técnico', 150.00)
ON CONFLICT DO NOTHING;

-- ============================================
-- 6. PEÇAS/INSUMOS (estoque)
-- ============================================
INSERT INTO peca_insumo (id, nome, descricao, preco_venda, preco_compra, quantidade_por_unidade, quantidade_estoque, quantidade_minima) VALUES
('55555555-5555-4555-8555-555555555550', 'Óleo de Motor 5W30 Sintético', 'Lubrificante sintético API SN para motores flex, embalagem de 1 litro', 59.90, 36.50, 'Litro', 40, 10),
('55555555-5555-4555-8555-555555555551', 'Filtro de Óleo', 'Filtro de óleo spin-on para motores 1.0 a 2.0', 42.00, 24.80, 'Unidade', 35, 15),
('55555555-5555-4555-8555-555555555552', 'Pastilha de Freio Dianteira', 'Jogo de pastilhas cerâmicas para eixo dianteiro', 189.00, 120.00, 'Jogo com 4', 28, 8),
('55555555-5555-4555-8555-555555555553', 'Disco de Freio Ventilado', 'Par de discos ventilados para veículos compactos e médios', 329.90, 220.00, 'Par', 22, 5),
('55555555-5555-4555-8555-555555555554', 'Fluido de Arrefecimento', 'Aditivo pronto uso para sistema de arrefecimento, frasco de 1 litro', 34.90, 18.50, 'Litro', 30, 10)
ON CONFLICT DO NOTHING;

-- ============================================
-- 7. ORDENS DE SERVIÇO (atendimento)
-- ============================================
INSERT INTO ordens_servico (id, cliente_id, veiculo_id, mecanico_id, status, valor_total_acumulado, valor_total, urgente, queixa_cliente, observacoes, data_criacao, data_inicio_execucao, data_finalizacao) VALUES
(20260001, '88888888-8888-4888-8888-888888888880', '66666666-6666-4666-8666-666666666660', '77777777-7777-4777-8777-777777777771', 'EM_DIAGNOSTICO', 0.00, 0.00, FALSE, 'Barulho anormal ao frear', 'Veículo possui risco aproximado de 45cm na lateral esquerda', CURRENT_TIMESTAMP - INTERVAL '1 day', NULL, NULL),
(20260002, '88888888-8888-4888-8888-888888888881', '66666666-6666-4666-8666-666666666661', '77777777-7777-4777-8777-777777777771', 'AGUARDANDO_APROVACAO', 0.00, 0.00, FALSE, 'Alinhamento desagradável', 'Carro puxa para a esquerda em retas', CURRENT_TIMESTAMP - INTERVAL '12 hours', NULL, NULL),
(20260003, '88888888-8888-4888-8888-888888888882', '66666666-6666-4666-8666-666666666662', '77777777-7777-4777-8777-777777777771', 'EM_EXECUCAO', 0.00, 0.00, FALSE, 'Fluido de arrefecimento', 'Verificar vazamento no radiador', CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '3 hours', NULL),
(20260004, '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440003', '77777777-7777-4777-8777-777777777771', 'EM_DIAGNOSTICO', 0.00, 0.00, FALSE, 'Diagnóstico inicial do veículo', 'Ordem de serviço aberta para diagnóstico do Ford Ka', CURRENT_TIMESTAMP - INTERVAL '2 days', NULL, NULL)
ON CONFLICT DO NOTHING;

-- ============================================
-- 8. ORÇAMENTOS (atendimento)
-- ============================================
INSERT INTO os_orcamentos (id, ordem_servico_id, tipo, status, valor_total, prazo_estipulado, data_criacao) VALUES
(26000001, 20260001, 'INICIAL', 'PENDENTE', 0.00, CURRENT_TIMESTAMP + INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(26000002, 20260002, 'INICIAL', 'PENDENTE', 280.00, CURRENT_TIMESTAMP + INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '12 hours'),
(26000003, 20260003, 'INICIAL', 'APROVADO', 400.00, CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '6 hours'),
(26000004, 20260004, 'INICIAL', 'PENDENTE', 0.00, CURRENT_TIMESTAMP + INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '2 days')
ON CONFLICT DO NOTHING;

-- ============================================
-- 9. ITENS DO ORÇAMENTO - SERVIÇOS
-- ============================================
INSERT INTO os_servicos (id, orcamento_id, servico_id, preco_mao_de_obra_aplicado
                        , ordem_servico_id) VALUES
('99999999-9999-4999-8999-999999999921', 26000001, '77777777-7777-4777-8777-777777777773', 120.00, 20260001),
('99999999-9999-4999-8999-999999999922', 26000002, '77777777-7777-4777-8777-777777777774', 180.00, 20260002),
('99999999-9999-4999-8999-999999999923', 26000002, '77777777-7777-4777-8777-777777777775', 160.00, 20260002),
('99999999-9999-4999-8999-999999999924', 26000003, '77777777-7777-4777-8777-777777777776', 150.00, 20260003),
('99999999-9999-4999-8999-999999999925', 26000004, '77777777-7777-4777-8777-777777777773', 120.00, 20260004)
ON CONFLICT DO NOTHING;

-- ============================================
-- 10. ITENS DO ORÇAMENTO - PEÇAS
-- ============================================
INSERT INTO os_pecas (id, orcamento_id, peca_id, quantidade, preco_venda_aplicado, ordem_servico_id) VALUES
('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa1', 26000001, '55555555-5555-4555-8555-555555555550', 1, 59.90, 20260001),
('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa2', 26000001, '55555555-5555-4555-8555-555555555551', 1, 42.00, 20260001),
('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa3', 26000002, '55555555-5555-4555-8555-555555555552', 1, 189.00, 20260002),
('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa4', 26000003, '55555555-5555-4555-8555-555555555553', 1, 329.90, 20260003),
('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa5', 26000003, '55555555-5555-4555-8555-555555555554', 1, 34.90, 20260003),
('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa6', 26000004, '55555555-5555-4555-8555-555555555550', 1, 59.90, 20260004),
('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa7', 26000004, '55555555-5555-4555-8555-555555555551', 1, 42.00, 20260004)
ON CONFLICT DO NOTHING;
