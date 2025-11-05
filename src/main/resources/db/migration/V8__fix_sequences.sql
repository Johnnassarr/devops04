-- V8__fix_sequences.sql
-- Corrige as sequences do PostgreSQL após inserções com IDs fixos
-- Usa pg_get_serial_sequence para descobrir automaticamente o nome correto da sequence

DO $$
DECLARE
    seq_name TEXT;
BEGIN
    -- Corrigir sequence de galpoes
    seq_name := pg_get_serial_sequence('galpoes', 'id');
    IF seq_name IS NOT NULL AND EXISTS (SELECT 1 FROM galpoes) THEN
        EXECUTE format('SELECT setval(%L, (SELECT COALESCE(MAX(id), 1) FROM galpoes), true)', seq_name);
    END IF;

    -- Corrigir sequence de motoqueiros
    seq_name := pg_get_serial_sequence('motoqueiros', 'id');
    IF seq_name IS NOT NULL AND EXISTS (SELECT 1 FROM motoqueiros) THEN
        EXECUTE format('SELECT setval(%L, (SELECT COALESCE(MAX(id), 1) FROM motoqueiros), true)', seq_name);
    END IF;

    -- Corrigir sequence de manutencao
    seq_name := pg_get_serial_sequence('manutencao', 'id');
    IF seq_name IS NOT NULL AND EXISTS (SELECT 1 FROM manutencao) THEN
        EXECUTE format('SELECT setval(%L, (SELECT COALESCE(MAX(id), 1) FROM manutencao), true)', seq_name);
    END IF;

    -- Corrigir sequence de roles
    seq_name := pg_get_serial_sequence('roles', 'id');
    IF seq_name IS NOT NULL AND EXISTS (SELECT 1 FROM roles) THEN
        EXECUTE format('SELECT setval(%L, (SELECT COALESCE(MAX(id), 1) FROM roles), true)', seq_name);
    END IF;

    -- Corrigir sequence de usuarios
    seq_name := pg_get_serial_sequence('usuarios', 'id');
    IF seq_name IS NOT NULL AND EXISTS (SELECT 1 FROM usuarios) THEN
        EXECUTE format('SELECT setval(%L, (SELECT COALESCE(MAX(id), 1) FROM usuarios), true)', seq_name);
    END IF;

    -- Corrigir sequence de motos
    seq_name := pg_get_serial_sequence('motos', 'id');
    IF seq_name IS NOT NULL AND EXISTS (SELECT 1 FROM motos) THEN
        EXECUTE format('SELECT setval(%L, (SELECT COALESCE(MAX(id), 1) FROM motos), true)', seq_name);
    END IF;
END $$;

