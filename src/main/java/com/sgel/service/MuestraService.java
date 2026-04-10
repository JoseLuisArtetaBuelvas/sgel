package com.sgel.service;

import com.sgel.model.Muestra;
import com.sgel.repository.MuestraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MuestraService {

    private final MuestraRepository muestraRepository;

    public MuestraService(MuestraRepository muestraRepository) {
        this.muestraRepository = muestraRepository;
    }

    // Registrar muestra con validación de código único (HU-01)
    @Transactional
    public Muestra registrarMuestra(Muestra muestra) {
        // TC-02: código duplicado → lanzar excepción
        if (muestraRepository.existsByCodigo(muestra.getCodigo())) {
            throw new RuntimeException("Ya existe una muestra con el código: " + muestra.getCodigo());
        }
        return muestraRepository.save(muestra);
    }

    @Transactional(readOnly = true)
    public List<Muestra> listarMuestrasActivas() {
        return muestraRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Muestra buscarPorCodigo(String codigo) {
        return muestraRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Muestra no encontrada con código: " + codigo));
    }

    @Transactional(readOnly = true)
    public Muestra buscarPorId(Long id) {
        return muestraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Muestra no encontrada con id: " + id));
    }
}
