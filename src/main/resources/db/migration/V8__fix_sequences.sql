-- V8__fix_sequences.sql
-- Corrige as sequences do PostgreSQL após inserções com IDs fixos
-- Atualiza apenas se a tabela tiver registros

DO $$
BEGIN
    -- Corrigir sequence de galpoes
    IF EXISTS (SELECT 1 FROM galpoes) THEN
        PERFORM setval('galpoes_id_seq', (SELECT COALESCE(MAX(id), 1) FROM galpoes), true);
    END IF;

    -- Corrigir sequence de motoqueiros
    IF EXISTS (SELECT 1 FROM motoqueiros) THEN
        PERFORM setval('motoqueiros_id_seq', (SELECT COALESCE(MAX(id), 1) FROM motoqueiros), true);
    END IF;

    -- Corrigir sequence de manutencao
    IF EXISTS (SELECT 1 FROM manutencao) THEN
        PERFORM setval('manutencao_id_seq', (SELECT COALESCE(MAX(id), 1) FROM manutencao), true);
    END IF;

    -- Corrigir sequence de roles
    IF EXISTS (SELECT 1 FROM roles) THEN
        PERFORM setval('roles_id_seq', (SELECT COALESCE(MAX(id), 1) FROM roles), true);
    END IF;

    -- Corrigir sequence de usuarios
    IF EXISTS (SELECT 1 FROM usuarios) THEN
        PERFORM setval('usuarios_id_seq', (SELECT COALESCE(MAX(id), 1) FROM usuarios), true);
    END IF;

    -- Corrigir sequence de motos
    IF EXISTS (SELECT 1 FROM motos) THEN
        PERFORM setval('motos_id_seq', (SELECT COALESCE(MAX(id), 1) FROM motos), true);
    END IF;
END $$;

