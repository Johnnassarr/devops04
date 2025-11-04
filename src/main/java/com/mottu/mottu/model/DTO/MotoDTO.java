    package com.mottu.mottu.model.DTO;


    import com.mottu.mottu.model.AnoMoto;
    import com.mottu.mottu.model.ModeloMoto;
    import com.mottu.mottu.model.StatusMoto;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Size;

    import java.time.LocalDateTime;

    public class MotoDTO {
        private Long id;  // adicionado

        @NotBlank(message = "A placa é obrigatória")
        @Size(min = 7, max = 8, message = "A placa deve ter entre 7 e 8 caracteres")
        private String placa;

        @NotNull(message = "O modelo é obrigatório")
        private ModeloMoto modelo;

        @NotNull(message = "O ano é obrigatório")
        private AnoMoto ano;

        @NotNull(message = "O status é obrigatório")
        private StatusMoto status;

        private LocalDateTime dataSaida;

        private LocalDateTime dataRetorno;

        private Long motoboyId;

        @NotNull(message = "O galpão é obrigatório")
        private Long galpaoId;

        @NotNull(message = "Informe se está em manutenção")
        private Boolean emManutencao;

        // Getters e setters
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

        public Long getMotoboyId() {
            return motoboyId;
        }

        public void setMotoboyId(Long motoboyId) {
            this.motoboyId = motoboyId;
        }

        public Long getGalpaoId() {
            return galpaoId;
        }

        public void setGalpaoId(Long galpaoId) {
            this.galpaoId = galpaoId;
        }

        public Boolean getEmManutencao() {
            return emManutencao;
        }

        public void setEmManutencao(Boolean emManutencao) {
            this.emManutencao = emManutencao;
        }
    }
