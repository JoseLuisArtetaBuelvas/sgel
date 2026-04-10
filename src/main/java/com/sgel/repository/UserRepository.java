package com.sgel.repository;

import com.sgel.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Long> {

    // Usado por Spring Security para cargar el usuario al hacer login
    Optional<Usuario> findByUsername(String username);

    // Verificar si existe un username antes de crear uno nuevo
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
