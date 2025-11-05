-- Corrigir sequences após inserção manual de IDs

-- Atualizar sequence de roles
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles), true);

-- Atualizar sequence de usuarios
SELECT setval('usuarios_id_seq', (SELECT MAX(id) FROM usuarios), true);

-- Atualizar sequence de galpoes
SELECT setval('galpoes_id_seq', (SELECT MAX(id) FROM galpoes), true);

-- Atualizar sequence de motoqueiros
SELECT setval('motoqueiros_id_seq', (SELECT MAX(id) FROM motoqueiros), true);

-- Atualizar sequence de manutencao
SELECT setval('manutencao_id_seq', (SELECT MAX(id) FROM manutencao), true);

-- Atualizar sequence de motos
SELECT setval('motos_id_seq', (SELECT MAX(id) FROM motos), true);

