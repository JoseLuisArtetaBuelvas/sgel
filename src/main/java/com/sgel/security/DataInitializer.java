package com.sgel.security;

import com.sgel.model.Rol;
import com.sgel.repository.RolRepository;
import com.sgel.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Se ejecuta una sola vez al iniciar la aplicación.
 * Crea los roles base y el usuario administrador si no existen.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final AuthService authService;

    public DataInitializer(RolRepository rolRepository, AuthService authService) {
        this.rolRepository = rolRepository;
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        crearRolSiNoExiste("ADMIN",            "Administrador del sistema");
        crearRolSiNoExiste("DIRECTOR",         "Director de laboratorio");
        crearRolSiNoExiste("JEFE_LABORATORIO", "Jefe de laboratorio");
        crearRolSiNoExiste("ANALISTA",         "Analista de ensayos");
        crearRolSiNoExiste("CLIENTE",          "Cliente externo");

        // Crear usuario admin por defecto si no existe
        try {
            authService.crearUsuario("admin", "admin123", "admin@sgel.com", "ADMIN");
            System.out.println("✓ Usuario admin creado → usuario: admin / contraseña: admin123");
        } catch (RuntimeException e) {
            System.out.println("✓ Usuario admin ya existe, omitiendo creación.");
        }
    }

    private void crearRolSiNoExiste(String nombre, String descripcion) {
        if (rolRepository.findByNombre(nombre).isEmpty()) {
            rolRepository.save(new Rol(nombre, descripcion));
            System.out.println("✓ Rol creado: " + nombre);
        }
    }
}
