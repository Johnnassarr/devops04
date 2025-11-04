package com.mottu.mottu.service;


import com.mottu.mottu.model.DTO.ManutencaoDTO;
import com.mottu.mottu.model.Manutencao;
import com.mottu.mottu.repository.ManutencaoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManutencaoService {

    @Autowired
    private ManutencaoRepository manutencaoRepository;

    public List<Manutencao> listarTodos() {
        return manutencaoRepository.findAll();
    }

    public Manutencao salvar(ManutencaoDTO dto) {

        Manutencao manutencao = ManutencaoMapper.fromDTO(dto);
        return manutencaoRepository.save(manutencao);
    }

    public Optional<Manutencao> atualizar(Long id, ManutencaoDTO dto) {

        return manutencaoRepository.findById(id)
                .map(m -> {
                    ManutencaoMapper.updateFromDTO(dto, m);
                    return manutencaoRepository.save(m);
                });
    }

    public void deletar(Long id) {
        Manutencao manutencao = manutencaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Manutenção não encontrada"));
        manutencaoRepository.delete(manutencao);
    }

    public Optional<Manutencao> buscarPorId(Long id) {
        return manutencaoRepository.findById(id);
    }
}
