package com.veterinariawoof.controllers;

import com.veterinariawoof.models.HistoriaClinica;
import com.veterinariawoof.services.HistoriaClinicaService;
import com.veterinariawoof.services.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/historia")
public class HistoriaClinicaController {

    @Autowired
    private HistoriaClinicaService historiaService;

    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("historias", historiaService.obtenerTodas());
        return "historia/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("historia", new HistoriaClinica());
        model.addAttribute("mascotas", mascotaService.obtenerTodas());
        return "historia/formulario";
    }

    @GetMapping("/ingreso")
    public String ingreso(Model model) {
        model.addAttribute("historia", new HistoriaClinica());
        model.addAttribute("mascotas", mascotaService.obtenerTodas());
        return "historia/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute HistoriaClinica historia) {
        historiaService.guardar(historia);
        return "redirect:/historia";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("historia", historiaService.obtenerPorId(id));
        model.addAttribute("mascotas", mascotaService.obtenerTodas());
        return "historia/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        historiaService.eliminar(id);
        return "redirect:/historia";
    }
}
