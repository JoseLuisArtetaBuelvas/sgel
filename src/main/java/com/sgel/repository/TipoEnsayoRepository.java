package com.sgel.repository;

import com.sgel.model.TipoEnsayo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoEnsayoRepository extends JpaRepository<TipoEnsayo, Long> {
    boolean existsByCodigo(String codigo);
}
