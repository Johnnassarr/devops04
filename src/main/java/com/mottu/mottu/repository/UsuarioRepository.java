package com.mottu.mottu.repository;

import com.mottu.mottu.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    com.mottu.mottu.model.Usuario findByEmail(String email);
    boolean existsByEmail(String email);
}
