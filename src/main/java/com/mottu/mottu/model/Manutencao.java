package com.mottu.mottu.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity(name = "manutencao")
@Table(name = "manutencao")
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private PrioridadeManutencao prioridadeManutencao = PrioridadeManutencao.MEDIA;

    @PastOrPresent(message = "Data de abertura não pode ser futura")
    private LocalDateTime dataAbertura;

    private LocalDateTime dataFechamento;

    private boolean emAndamento;

    private String placaMoto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
