package com.sgel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "muestras")
@Getter @Setter @NoArgsConstructor
public class Muestra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código único de identificación de la muestra
    @NotBlank(message = "El código es obligatorio")
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @NotBlank(message = "El producto es obligatorio")
    @Column(nullable = false, length = 100)
    private String producto;

    @Column(length = 50)
    private String lote;

    @Column(name = "fecha_recepcion")
    private LocalDate fechaRecepcion = LocalDate.now();

    // Username del Jefe de Laboratorio que registra la muestra
    @Column(nullable = false, length = 50)
    private String responsable;

    @Column(nullable = false)
    private Boolean activo = true;

    // Una muestra puede tener muchos ensayos
    @OneToMany(mappedBy = "muestra", cascade = CascadeType.ALL)
    private List<Ensayo> ensayos = new ArrayList<>();
}
