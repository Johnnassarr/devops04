CREATE TABLE manutencao (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    prioridade_manutencao VARCHAR(20) NOT NULL DEFAULT 'MEDIA',
    data_abertura TIMESTAMP CHECK (data_abertura <= CURRENT_TIMESTAMP),
    data_fechamento TIMESTAMP CHECK (data_fechamento <= CURRENT_TIMESTAMP),
    em_andamento BOOLEAN NOT NULL DEFAULT TRUE,
    placa_moto VARCHAR(20)
);