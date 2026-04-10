package com.sgel.controller;

import com.sgel.model.Usuario;
import com.sgel.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Página de login (GET /login)
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "Usuario o contraseña incorrectos.");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "Sesión cerrada correctamente.");
        }
        return "login";  // → templates/login.html
    }

    // Dashboard principal después del login
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
                            Model model,
                            HttpServletRequest request) {
        model.addAttribute("usuario", userDetails.getUsername());
        model.addAttribute("roles", userDetails.getAuthorities());

        // Registrar acceso en log (HU-06 TC-06)
        authService.registrarAcceso(
            userDetails.getUsername(),
            "INGRESO_DASHBOARD",
            request.getRemoteAddr()
        );
        return "dashboard";  // → templates/dashboard.html
    }

    // ── API REST para crear usuario (ADMIN) ──────────────────────────────────

    @PostMapping("/api/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> crearUsuario(@RequestBody Map<String, String> body) {
        try {
            Usuario nuevo = authService.crearUsuario(
                body.get("username"),
                body.get("password"),
                body.get("email"),
                body.get("rol")
            );
            return ResponseEntity.ok(Map.of(
                "mensaje", "Usuario creado exitosamente",
                "id", nuevo.getId(),
                "username", nuevo.getUsername()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── API REST para desactivar usuario (ADMIN) ─────────────────────────────

    @PutMapping("/api/usuarios/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> desactivarUsuario(@PathVariable Long id) {
        try {
            authService.desactivarUsuario(id);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario desactivado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
