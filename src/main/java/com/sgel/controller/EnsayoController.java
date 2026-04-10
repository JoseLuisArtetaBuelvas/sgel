package com.sgel.controller;

import com.sgel.model.Ensayo;
import com.sgel.repository.TipoEnsayoRepository;
import com.sgel.service.EnsayoService;
import com.sgel.service.MuestraService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class EnsayoController {

    private final EnsayoService ensayoService;
    private final MuestraService muestraService;
    private final TipoEnsayoRepository tipoEnsayoRepository;

    public EnsayoController(EnsayoService ensayoService,
                             MuestraService muestraService,
                             TipoEnsayoRepository tipoEnsayoRepository) {
        this.ensayoService = ensayoService;
        this.muestraService = muestraService;
        this.tipoEnsayoRepository = tipoEnsayoRepository;
    }

    // ── Vista web (Thymeleaf) ─────────────────────────────────────────────────

    @GetMapping("/ensayos/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','JEFE_LABORATORIO','ANALISTA')")
    public String formularioNuevo(Model model) {
        model.addAttribute("muestras", muestraService.listarMuestrasActivas());
        model.addAttribute("tiposEnsayo", tipoEnsayoRepository.findAll());
        return "ensayos/nuevo";  // → templates/ensayos/nuevo.html
    }

    @PostMapping("/ensayos/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','JEFE_LABORATORIO','ANALISTA')")
    public String crearEnsayo(@RequestParam Long muestraId,
                               @RequestParam Long tipoEnsayoId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttrs,
                               Model model) {
        try {
            ensayoService.crearEnsayo(muestraId, tipoEnsayoId, userDetails.getUsername());
            redirectAttrs.addFlashAttribute("exito", "Ensayo creado correctamente.");
            return "redirect:/ensayos/nuevo";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("muestras", muestraService.listarMuestrasActivas());
            model.addAttribute("tiposEnsayo", tipoEnsayoRepository.findAll());
            return "ensayos/nuevo";
        }
    }

    // ── API REST ──────────────────────────────────────────────────────────────

    @PostMapping("/api/ensayos")
    @PreAuthorize("hasAnyRole('ADMIN','JEFE_LABORATORIO','ANALISTA')")
    @ResponseBody
    public ResponseEntity<?> crearEnsayoApi(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long muestraId = Long.valueOf(body.get("muestraId").toString());
            Long tipoId    = Long.valueOf(body.get("tipoEnsayoId").toString());

            Ensayo ensayo = ensayoService.crearEnsayo(muestraId, tipoId, userDetails.getUsername());
            return ResponseEntity.ok(Map.of(
                "mensaje", "Ensayo creado",
                "id", ensayo.getId(),
                "estado", ensayo.getEstado(),
                "fechaInicio", ensayo.getFechaInicio().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/ensayos/muestra/{muestraId}")
    @ResponseBody
    public ResponseEntity<?> listarPorMuestra(@PathVariable Long muestraId) {
        return ResponseEntity.ok(ensayoService.listarEnsayosPorMuestra(muestraId));
    }
}
