package com.sgel.service;

import com.sgel.model.LogAcceso;
import com.sgel.model.Rol;
import com.sgel.model.Usuario;
import com.sgel.repository.LogAccesoRepository;
import com.sgel.repository.RolRepository;
import com.sgel.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final LogAccesoRepository logAccesoRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       RolRepository rolRepository,
                       LogAccesoRepository logAccesoRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.logAccesoRepository = logAccesoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Spring Security llama este método al hacer login
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario desactivado: " + username);
        }

        // Convertir roles a GrantedAuthority para Spring Security
        Set<SimpleGrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                .collect(Collectors.toSet());

        return new User(usuario.getUsername(), usuario.getPassword(), authorities);
    }

    // Crear un nuevo usuario (usado por el administrador)
    @Transactional
    public Usuario crearUsuario(String username, String password, String email, String nombreRol) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("El username '" + username + "' ya existe");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("El email '" + email + "' ya está registrado");
        }

        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol));

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setEmail(email);
        usuario.setActivo(true);
        usuario.getRoles().add(rol);

        return userRepository.save(usuario);
    }

    // Desactivar usuario (TC-05 de HU-06)
    @Transactional
    public void desactivarUsuario(Long usuarioId) {
        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        userRepository.save(usuario);
    }

    // Registrar log de acceso (TC-06 de HU-06)
    @Transactional
    public void registrarAcceso(String username, String accion, String ip) {
        userRepository.findByUsername(username).ifPresent(usuario -> {
            LogAcceso log = new LogAcceso(usuario, accion, ip);
            logAccesoRepository.save(log);
        });
    }
}
