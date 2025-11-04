package com.mottu.mottu.model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class GalpaoDTO {

    @NotBlank(message = "O nome não pode estar vazio")
    private String nome;

    @NotBlank(message = "O endereço não pode estar vazio")
    private String endereco;

    @Positive(message = "A capacidade deve ser maior que zero")
    private int capacidade;

    public String getNome() {
        return nome = nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco = endereco;
    }

    public void setEndereco(String endereco) {
        this. endereco =  endereco;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }
}
