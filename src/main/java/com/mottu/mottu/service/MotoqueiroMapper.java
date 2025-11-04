package com.mottu.mottu.service;


import com.mottu.mottu.model.DTO.MotoqueiroDTO;
import com.mottu.mottu.model.Motoqueiro;
import org.springframework.beans.BeanUtils;

public class MotoqueiroMapper {

    public static Motoqueiro fromDTO(MotoqueiroDTO dto) {
        Motoqueiro motoqueiro = new Motoqueiro();
        BeanUtils.copyProperties(dto, motoqueiro);
        return motoqueiro;
    }

    public static void updateFromDTO(MotoqueiroDTO dto, Motoqueiro motoqueiro) {
        BeanUtils.copyProperties(dto, motoqueiro);
    }

    public static MotoqueiroDTO toDTO(Motoqueiro motoqueiro) {
        MotoqueiroDTO dto = new MotoqueiroDTO();
        BeanUtils.copyProperties(motoqueiro, dto);
        return dto;
    }
}
