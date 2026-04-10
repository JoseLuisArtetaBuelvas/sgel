package com.sgel.repository;

import com.sgel.model.Muestra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MuestraRepository extends JpaRepository<Muestra, Long> {

    // Verificar código único antes de guardar (HU-01 TC-02)
    boolean existsByCodigo(String codigo);

    Optional<Muestra> findByCodigo(String codigo);

    // Listar muestras activas de un responsable
    List<Muestra> findByResponsableAndActivoTrue(String responsable);

    List<Muestra> findByActivoTrue();
}
