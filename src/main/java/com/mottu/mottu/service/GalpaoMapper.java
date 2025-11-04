package com.mottu.mottu.service;

import com.mottu.mottu.model.DTO.GalpaoDTO;
import com.mottu.mottu.model.Galpao;
import org.springframework.beans.BeanUtils;

public class GalpaoMapper {

    public static Galpao fromDTO(GalpaoDTO dto) {
        Galpao galpao = new Galpao();
        BeanUtils.copyProperties(dto, galpao);
        return galpao;
    }

    public static void updateFromDTO(GalpaoDTO dto, Galpao galpao) {
        BeanUtils.copyProperties(dto, galpao);
    }

    public static GalpaoDTO toDTO(Galpao galpao) {
        GalpaoDTO dto = new GalpaoDTO();
        BeanUtils.copyProperties(galpao, dto);
        return dto;
    }
}
