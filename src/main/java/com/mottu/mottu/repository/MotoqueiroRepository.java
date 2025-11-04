package com.mottu.mottu.repository;

import com.mottu.mottu.model.Motoqueiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MotoqueiroRepository extends JpaRepository<Motoqueiro, Long> {

}
