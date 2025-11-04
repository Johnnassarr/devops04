package com.mottu.mottu.service;


import com.mottu.mottu.model.DTO.MotoDTO;
import com.mottu.mottu.model.Moto;
import org.springframework.beans.BeanUtils;

public class MotoMapper {

    public static Moto fromDTO(MotoDTO dto) {
        Moto moto = new Moto();
        BeanUtils.copyProperties(dto, moto);
        return moto;
    }

    public static void updateFromDTO(MotoDTO dto, Moto moto) {
        BeanUtils.copyProperties(dto, moto, "id");
    }

    public static MotoDTO toDTO(Moto moto) {
        MotoDTO dto = new MotoDTO();
        BeanUtils.copyProperties(moto, dto);
        return dto;
    }
}