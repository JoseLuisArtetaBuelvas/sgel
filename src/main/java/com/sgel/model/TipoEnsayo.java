package com.sgel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipos_ensayo")
@Getter @Setter @NoArgsConstructor
public class TipoEnsayo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    // Condiciones mínimas almacenadas como texto (JSON o descripción textual)
    @Column(name = "condiciones_minimas", columnDefinition = "TEXT")
    private String condicionesMinimas;
}
