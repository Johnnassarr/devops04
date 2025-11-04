package com.mottu.mottu.service;


import com.mottu.mottu.model.DTO.ManutencaoDTO;
import com.mottu.mottu.model.Manutencao;
import org.springframework.beans.BeanUtils;

public class ManutencaoMapper {

    public static Manutencao fromDTO(ManutencaoDTO dto) {
        Manutencao manutencao = new Manutencao();
        BeanUtils.copyProperties(dto, manutencao);
        return manutencao;
    }

    public static void updateFromDTO(ManutencaoDTO dto, Manutencao manutencao) {
        BeanUtils.copyProperties(dto, manutencao, "id");
    }

    public static ManutencaoDTO toDTO(Manutencao manutencao) {
        ManutencaoDTO dto = new ManutencaoDTO();
        BeanUtils.copyProperties(manutencao, dto);
        return dto;
    }

}
