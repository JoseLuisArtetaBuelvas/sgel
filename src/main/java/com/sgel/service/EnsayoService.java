package com.sgel.service;

import com.sgel.model.Ensayo;
import com.sgel.model.Muestra;
import com.sgel.model.TipoEnsayo;
import com.sgel.repository.EnsayoRepository;
import com.sgel.repository.MuestraRepository;
import com.sgel.repository.TipoEnsayoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnsayoService {

    private final EnsayoRepository ensayoRepository;
    private final MuestraRepository muestraRepository;
    private final TipoEnsayoRepository tipoEnsayoRepository;

    public EnsayoService(EnsayoRepository ensayoRepository,
                         MuestraRepository muestraRepository,
                         TipoEnsayoRepository tipoEnsayoRepository) {
        this.ensayoRepository = ensayoRepository;
        this.muestraRepository = muestraRepository;
        this.tipoEnsayoRepository = tipoEnsayoRepository;
    }

    // Crear ensayo vinculado a una muestra existente (HU-02)
    @Transactional
    public Ensayo crearEnsayo(Long muestraId, Long tipoEnsayoId, String analista) {
        // TC-03: muestra inexistente → excepción
        Muestra muestra = muestraRepository.findById(muestraId)
                .orElseThrow(() -> new RuntimeException("Muestra no encontrada con id: " + muestraId));

        // TC-04: tipo de ensayo obligatorio
        TipoEnsayo tipo = tipoEnsayoRepository.findById(tipoEnsayoId)
                .orElseThrow(() -> new RuntimeException("Tipo de ensayo no encontrado con id: " + tipoEnsayoId));

        Ensayo ensayo = new Ensayo();
        ensayo.setMuestra(muestra);
        ensayo.setTipoEnsayo(tipo);
        ensayo.setAnalista(analista);
        ensayo.setFechaInicio(LocalDate.now()); // TC-06: fecha automática
        ensayo.setEstado("PENDIENTE");

        // TC-05: el ID se asigna automáticamente por la BD (GenerationType.IDENTITY)
        return ensayoRepository.save(ensayo);
    }

    @Transactional(readOnly = true)
    public List<Ensayo> listarEnsayosPorMuestra(Long muestraId) {
        return ensayoRepository.findByMuestraId(muestraId);
    }

    @Transactional(readOnly = true)
    public Ensayo buscarPorId(Long id) {
        return ensayoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ensayo no encontrado con id: " + id));
    }
}
