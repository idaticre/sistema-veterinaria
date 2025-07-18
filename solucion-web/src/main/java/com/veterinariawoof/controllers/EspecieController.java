package com.veterinariawoof.controllers;

import com.veterinariawoof.models.Especie;
import com.veterinariawoof.services.EspecieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/especies")
public class EspecieController {

    @Autowired
    private EspecieService especieService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("especies", especieService.obtenerTodas());
        return "especies/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("especie", new Especie());
        return "especies/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Especie especie) {
        especieService.guardar(especie);
        return "redirect:/especies";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("especie", especieService.obtenerPorId(id));
        return "especies/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        especieService.eliminar(id);
        return "redirect:/especies";
    }
}
