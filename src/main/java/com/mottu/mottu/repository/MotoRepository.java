package com.mottu.mottu.repository;

import com.mottu.mottu.model.Moto;
import com.mottu.mottu.model.Motoqueiro;
import com.mottu.mottu.model.StatusMoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotoRepository extends JpaRepository<Moto, Long> {
    boolean existsByMotoboyEmUsoId(Long id);
    boolean existsByGalpaoId(Long galpaoId);
    boolean existsByMotoboyEmUsoAndStatus(Motoqueiro motoqueiro, StatusMoto status);
}
