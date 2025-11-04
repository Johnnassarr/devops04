package com.mottu.mottu.model.DTO;

import com.mottu.mottu.model.PrioridadeManutencao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ManutencaoDTO {

    private Long id;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 255, message = "Descrição muito longa")
    private String descricao;

    @NotNull(message = "Prioridade é obrigatória")
    private PrioridadeManutencao prioridadeManutencao = PrioridadeManutencao.MEDIA;

    @NotNull(message = "Data de abertura é obrigatória")
    @PastOrPresent(message = "Data de abertura não pode ser futura")
    private LocalDateTime dataAbertura;

    private LocalDateTime dataFechamento;

    private boolean emAndamento;

    @NotBlank(message = "Placa da moto é obrigatória")
    @Size(min = 7, max = 7, message = "Placa deve ter 7 caracteres")
    private String placaMoto;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public PrioridadeManutencao getPrioridadeManutencao() {
        return prioridadeManutencao;
    }

    public void setPrioridadeManutencao(PrioridadeManutencao prioridadeManutencao) {
        this.prioridadeManutencao = prioridadeManutencao;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public boolean isEmAndamento() {
        return emAndamento;
    }

    public void setEmAndamento(boolean emAndamento) {
        this.emAndamento = emAndamento;
    }

    public String getPlacaMoto() { return placaMoto; }

    public void setPlacaMoto(String placaMoto) { this.placaMoto = placaMoto; }
}
