CREATE TABLE motoqueiros (
    id BIGSERIAL PRIMARY KEY,
    nomeCompleto VARCHAR(255) NOT NULL,
    cpf VARCHAR(255) NOT NULL,
    telefone VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL
);

