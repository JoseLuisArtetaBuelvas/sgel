package com.sgel.repository;

import com.sgel.model.Ensayo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnsayoRepository extends JpaRepository<Ensayo, Long> {

    List<Ensayo> findByMuestraId(Long muestraId);

    List<Ensayo> findByAnalista(String analista);

    List<Ensayo> findByEstado(String estado);
}
