package com.mottu.mottu.service;

import com.mottu.mottu.model.DTO.GalpaoDTO;
import com.mottu.mottu.model.Galpao;
import com.mottu.mottu.repository.GalpaoRepository;
import com.mottu.mottu.repository.MotoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GalpaoService {

    @Autowired
    private MotoRepository motoRepository;

    private final GalpaoRepository galpaoRepository;

    public GalpaoService(GalpaoRepository galpaoRepository) {
        this.galpaoRepository = galpaoRepository;
    }

    public List<Galpao> listarTodos() {
        return galpaoRepository.findAll();
    }

    public Galpao salvar(GalpaoDTO dto) {

        Galpao galpao = GalpaoMapper.fromDTO(dto);
        return galpaoRepository.save(galpao);
    }

    public Optional<Galpao> editar(Long id, GalpaoDTO dto) {

        return galpaoRepository.findById(id)
                .map(g -> {
                    GalpaoMapper.updateFromDTO(dto, g);
                    return galpaoRepository.save(g);
                });
    }

    public boolean excluir(Long id) {
        return galpaoRepository.findById(id)
                .map(g -> {
                    validarExclusao(id);
                    galpaoRepository.delete(g);
                    return true;
                }).orElse(false);
    }

    public Optional<Galpao> buscarPorId(Long id) {
        return galpaoRepository.findById(id);
    }

    private void validarExclusao(Long id) {
        if (motoRepository.existsByGalpaoId(id)) {
            throw new IllegalStateException("Impossível excluir: Este galpão está sendo usado em uma moto ou mais motos.");
        }
    }
}
