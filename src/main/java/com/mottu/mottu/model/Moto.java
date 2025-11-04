package com.mottu.mottu.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity(name = "motos")
@Table(name = "motos")
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A placa é obrigatória")
    @Size(min = 7, max = 8, message = "A placa deve ter entre 7 e 8 caracteres")
    private String placa;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O modelo é obrigatório")
    private ModeloMoto modelo;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O ano é obrigatório")
    private AnoMoto ano;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O status é obrigatório")
    private StatusMoto status;

    private LocalDateTime dataSaida;

    private LocalDateTime dataRetorno;

    @NotNull(message = "Informe se está em manutenção")
    private boolean emManutencao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "motoboy_id")
    private Motoqueiro motoboyEmUso;

    @ManyToOne(optional = false)
    @JoinColumn(name = "galpao_id", nullable = false)
    private Galpao galpao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public ModeloMoto getModelo() {
        return modelo;
    }

    public void setModelo(ModeloMoto modelo) {
        this.modelo = modelo;
    }

    public AnoMoto getAno() {
        return ano;
    }

    public void setAno(AnoMoto ano) {
        this.ano = ano;
    }

    public StatusMoto getStatus() {
        return status;
    }

    public void setStatus(StatusMoto status) {
        this.status = status;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

    public LocalDateTime getDataRetorno() {
        return dataRetorno;
    }

    public void setDataRetorno(LocalDateTime dataRetorno) {
        this.dataRetorno = dataRetorno;
    }

    public Motoqueiro getMotoboyEmUso() {
        return motoboyEmUso;
    }

    public void setMotoboyEmUso(Motoqueiro motoboyEmUso) {
        this.motoboyEmUso = motoboyEmUso;
    }

    public Galpao getGalpao() {
        return galpao;
    }

    public void setGalpao(Galpao galpao) {
        this.galpao = galpao;
    }

    public boolean isEmManutencao() {return emManutencao;}

    public void setEmManutencao(boolean emManutencao) {
        this.emManutencao = emManutencao;
    }

    public void atribuirMotoqueiroEmUmaMoto(Motoqueiro motoqueiro) {
        if (this.status != StatusMoto.TRANSITO) {
            throw new IllegalStateException("Só é possível atribuir um motoqueiro se a moto estiver EM_USO");
        }
        this.motoboyEmUso = motoqueiro;
    }

    public void liberarMoto() {
        this.motoboyEmUso = null;
        this.status = StatusMoto.DISPONIVEL;
    }
}
