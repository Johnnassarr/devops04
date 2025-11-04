-- Inserir Roles
INSERT INTO roles (id, nome) VALUES (1, 'ADMIN');
INSERT INTO roles (id, nome) VALUES (2, 'OPERADOR');

-- Inserir Usuários
INSERT INTO usuarios (id, email, username, password, role_id)
VALUES (1, 'admin1@test.com', 'admin1', 'senha123', 1),
       (2, 'operador1@test.com', 'operador1', 'senha123', 2);

-- Inserir Galpões
INSERT INTO galpoes (id, nome, endereco, capacidade)
VALUES (1, 'Galpão Central', 'Rua A, 100', 50),
       (2, 'Galpão Norte', 'Rua B, 200', 30);

-- Inserir Motoqueiros
INSERT INTO motoqueiros (id, nomeCompleto, cpf, telefone, ativo)
VALUES (1, 'João Silva', '11122233344', '11999999999', true),
       (2, 'Carlos Souza', '55566677788', '11988888888', true);

-- Inserir Manutenções
INSERT INTO manutencao (id, descricao, prioridade_manutencao, data_abertura, data_fechamento, em_andamento, placa_moto)
VALUES (1, 'Troca de óleo', 'BAIXA', '2025-09-20T08:00:00', '2025-09-21T12:00:00', false, 'ABC-1234'),
       (2, 'Revisão freios', 'ALTA', '2025-09-19T09:00:00', NULL, true, 'XYZ-5678');

-- Inserir Motos (IDs gerados automaticamente)
INSERT INTO motos (placa, modelo, ano, status, data_saida, data_retorno, motoboy_id, galpao_id, em_manutencao)
VALUES
('ABC-1234', 'MOTTU_POP', 'ANO_2016', 'DISPONIVEL', NULL, NULL, NULL, 1, false),
('DEF-5678', 'MOTTU_E', 'ANO_2018', 'DISPONIVEL', NULL, NULL, NULL, 2, false),
('XYZ-5678', 'MOTTU_SPORT', 'ANO_2020', 'MANUTENCAO', NULL, NULL, NULL, 2, true),
('LMN-9012', 'MOTTU_POP', 'ANO_2018', 'TRANSITO', '2025-09-20T09:00:00', NULL, 1, 1, false);