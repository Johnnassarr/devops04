package com.mottu.mottu.service;

import com.mottu.mottu.model.DTO.MotoqueiroDTO;
import com.mottu.mottu.model.Motoqueiro;
import com.mottu.mottu.repository.MotoRepository;
import com.mottu.mottu.repository.MotoqueiroRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MotoqueiroService {

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private MotoqueiroRepository motoqueiroRepository;

    public List<Motoqueiro> listarTodos() {
        return motoqueiroRepository.findAll();
    }

    public Motoqueiro criarMotoqueiro(MotoqueiroDTO dto) {
        Motoqueiro motoqueiro = MotoqueiroMapper.fromDTO(dto);
        return motoqueiroRepository.save(motoqueiro);
    }

    public Optional<Motoqueiro> atualizarMotoqueiro(Long id, MotoqueiroDTO dto) {
        return motoqueiroRepository.findById(id)
                .map(m -> {
                    MotoqueiroMapper.updateFromDTO(dto, m);
                    return motoqueiroRepository.save(m);
                });
    }

    public boolean excluirMotoqueiro(Long id) {
        return motoqueiroRepository.findById(id)
                .map(m -> {
                    validarExclusao(id);
                    motoqueiroRepository.delete(m);
                    return true;
                }).orElse(false);
    }

    public Optional<Motoqueiro> buscarPorId(Long id) {
        return motoqueiroRepository.findById(id);
    }

    private void validarExclusao(Long id) {
        if (motoRepository.existsByMotoboyEmUsoId(id)) {
            throw new IllegalStateException("Impossível excluir: Motoqueiro está sendo usado em uma moto.");
        }
    }
}