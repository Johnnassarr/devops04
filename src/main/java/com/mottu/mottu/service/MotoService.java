package com.mottu.mottu.service;

import com.mottu.mottu.model.DTO.MotoDTO;
import com.mottu.mottu.model.Galpao;
import com.mottu.mottu.model.Moto;
import com.mottu.mottu.model.Motoqueiro;
import com.mottu.mottu.model.StatusMoto;
import com.mottu.mottu.repository.GalpaoRepository;
import com.mottu.mottu.repository.MotoRepository;
import com.mottu.mottu.repository.MotoqueiroRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MotoService {

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private GalpaoRepository galpaoRepository;

    @Autowired
    private MotoqueiroRepository motoqueiroRepository;

    public List<Moto> listarTodas() {
        return motoRepository.findAll();
    }

    public Moto salvar(MotoDTO dto) throws Exception {
        Moto moto = MotoMapper.fromDTO(dto); // usa o mapper
        processarRelacionamentos(moto, dto);
        validarStatusEHorarios(moto);
        return motoRepository.save(moto);
    }

    public Moto editar(Long id, MotoDTO dto) throws Exception {
        Moto moto = motoRepository.findById(id)
                .orElseThrow(() -> new Exception("Moto não encontrada"));
        MotoMapper.updateFromDTO(dto, moto); // atualiza sem alterar ID
        processarRelacionamentos(moto, dto);
        validarStatusEHorarios(moto);
        return motoRepository.save(moto);
    }

    public void excluir(Long id) throws Exception {
        Moto moto = motoRepository.findById(id)
                .orElseThrow(() -> new Exception("Moto não encontrada"));
        motoRepository.delete(moto);
    }

    public Optional<Moto> buscarPorId(Long id) {
        return motoRepository.findById(id);
    }

    // ---- Métodos auxiliares ----
    private void processarRelacionamentos(Moto moto, MotoDTO dto) throws Exception {
        // Galpão
        if (dto.getGalpaoId() != null) {
            Galpao galpao = galpaoRepository.findById(dto.getGalpaoId())
                    .orElseThrow(() -> new Exception("Galpão não encontrado"));
            moto.setGalpao(galpao);
        } else {
            moto.setGalpao(null);
        }

        // Motoqueiro
        if (dto.getStatus() == StatusMoto.TRANSITO) {
            if (dto.getMotoboyId() == null) throw new Exception("Moto em trânsito precisa ter um motoqueiro!");
            Motoqueiro motoqueiro = motoqueiroRepository.findById(dto.getMotoboyId())
                    .orElseThrow(() -> new Exception("Motoqueiro não encontrado"));
            moto.setMotoboyEmUso(motoqueiro);
        } else {
            moto.setMotoboyEmUso(null);
        }
    }

    private void validarStatusEHorarios(Moto moto) throws Exception {
        StatusMoto status = moto.getStatus();
        if (status == null) throw new Exception("Status da moto não pode ser nulo");

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime saida = moto.getDataSaida();
        LocalDateTime retorno = moto.getDataRetorno();

        switch (status) {
            case DISPONIVEL -> {
                if ((saida != null && retorno == null) || (saida == null && retorno != null))
                    throw new Exception("Se uma data for preenchida, ambas devem ser preenchidas.");

                if (saida != null && saida.isAfter(agora))
                    throw new Exception("Data de saída não pode ser futura.");

                if (retorno != null && retorno.isAfter(agora))
                    throw new Exception("Data de retorno não pode ser futura.");

                if (saida != null && retorno != null && saida.isAfter(retorno))
                    throw new Exception("Data de saída deve ser menor que a data de retorno.");

                moto.setEmManutencao(false);
            }
            case TRANSITO -> {
                if (saida == null) throw new Exception("Moto em trânsito precisa ter data de saída.");
                if (retorno != null) throw new Exception("Data de retorno deve ser vazia, o motoqueiro ainda está em trânsito.");
                if (saida.isAfter(agora)) throw new Exception("Data de saída não pode ser futura.");

                if (moto.getMotoboyEmUso() != null) {
                    boolean motoqueiroEmUso = motoRepository.existsByMotoboyEmUsoAndStatus(
                            moto.getMotoboyEmUso(), StatusMoto.TRANSITO);
                    if (motoqueiroEmUso)
                        throw new Exception("Este motoqueiro já está em trânsito com outra moto!");
                }

                moto.setEmManutencao(false);
            }
            case MANUTENCAO -> {
                if (saida != null || retorno != null)
                    throw new Exception("Moto em manutenção não pode ter datas de saída ou retorno.");
                moto.setEmManutencao(true);
                moto.setMotoboyEmUso(null);
            }
            default -> throw new Exception("Status da moto inválido");
        }
    }
}