CREATE TABLE motos (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    placa VARCHAR(20) NOT NULL,
    modelo VARCHAR(20) NOT NULL DEFAULT 'MOTTU_POP',
    ano VARCHAR(10),  -- Aqui armazenamos o enum AnoMoto
    status VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL',
    data_saida TIMESTAMP,
    data_retorno TIMESTAMP,
    em_manutencao BOOLEAN DEFAULT FALSE,
    motoboy_id BIGINT,
    galpao_id BIGINT NOT NULL,
    CONSTRAINT fk_motoboy FOREIGN KEY (motoboy_id) REFERENCES motoqueiros(id),
    CONSTRAINT fk_galpao FOREIGN KEY (galpao_id) REFERENCES galpoes(id)
);