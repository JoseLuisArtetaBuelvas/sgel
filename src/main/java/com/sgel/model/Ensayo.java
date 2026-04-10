package com.sgel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "ensayos")
@Getter @Setter @NoArgsConstructor
public class Ensayo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la muestra a la que pertenece este ensayo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "muestra_id", nullable = false)
    private Muestra muestra;

    // Tipo de ensayo según normativa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_ensayo_id")
    private TipoEnsayo tipoEnsayo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio = LocalDate.now();

    // Username del analista responsable
    @Column(nullable = false, length = 50)
    private String analista;

    // PENDIENTE, EN_PROCESO, COMPLETADO, RECHAZADO
    @Column(nullable = false, length = 30)
    private String estado = "PENDIENTE";

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
