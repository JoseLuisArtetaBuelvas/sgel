package com.sgel.controller;

import com.sgel.model.Muestra;
import com.sgel.service.MuestraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class MuestraController {

    private final MuestraService muestraService;

    public MuestraController(MuestraService muestraService) {
        this.muestraService = muestraService;
    }

    // ── Vista web (Thymeleaf) ─────────────────────────────────────────────────

    // Formulario de registro de muestra
    @GetMapping("/muestras/nueva")
    @PreAuthorize("hasAnyRole('ADMIN','JEFE_LABORATORIO')")
    public String formularioNueva(Model model) {
        model.addAttribute("muestra", new Muestra());
        model.addAttribute("muestrasRecientes", muestraService.listarMuestrasActivas());
        return "muestras/nueva";  // → templates/muestras/nueva.html
    }

    // Procesar formulario (POST desde HTML)
    @PostMapping("/muestras/nueva")
    @PreAuthorize("hasAnyRole('ADMIN','JEFE_LABORATORIO')")
    public String guardarMuestra(@Valid @ModelAttribute("muestra") Muestra muestra,
                                  BindingResult result,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttrs,
                                  Model model) {
        if (result.hasErrors()) {
            model.addAttribute("muestrasRecientes", muestraService.listarMuestrasActivas());
            return "muestras/nueva";
        }
        try {
            // TC-05: el responsable se asigna automáticamente
            muestra.setResponsable(userDetails.getUsername());
            muestraService.registrarMuestra(muestra);
            redirectAttrs.addFlashAttribute("exito", "Muestra registrada correctamente.");
            return "redirect:/muestras/nueva";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("muestrasRecientes", muestraService.listarMuestrasActivas());
            return "muestras/nueva";
        }
    }

    // ── API REST ──────────────────────────────────────────────────────────────

    @PostMapping("/api/muestras")
    @PreAuthorize("hasAnyRole('ADMIN','JEFE_LABORATORIO')")
    @ResponseBody
    public ResponseEntity<?> crearMuestra(
            @RequestBody Muestra muestra,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            muestra.setResponsable(userDetails.getUsername());
            Muestra guardada = muestraService.registrarMuestra(muestra);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Muestra registrada",
                "id", guardada.getId(),
                "codigo", guardada.getCodigo()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/muestras")
    @ResponseBody
    public ResponseEntity<List<Muestra>> listar() {
        return ResponseEntity.ok(muestraService.listarMuestrasActivas());
    }
}
