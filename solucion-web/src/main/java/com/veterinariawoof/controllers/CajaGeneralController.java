package com.veterinariawoof.controllers;

import com.veterinariawoof.models.CajaGeneral;
import com.veterinariawoof.services.CajaGeneralService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/caja")
public class CajaGeneralController {

    private final CajaGeneralService service;

    public CajaGeneralController(CajaGeneralService service) {
        this.service = service;
    }

    @GetMapping
    public String listarCaja(Model model) {
        model.addAttribute("movimientos", service.findAll());
        model.addAttribute("nuevoMovimiento", new CajaGeneral());
        return "caja/lista";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute CajaGeneral movimiento) {
        service.save(movimiento);
        return "redirect:/caja";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/caja";
    }
}
