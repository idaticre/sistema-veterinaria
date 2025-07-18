package com.veterinariawoof.controllers;

import com.veterinariawoof.models.CanalComunicacion;
import com.veterinariawoof.services.CanalComunicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/crm")
public class CanalComunicacionController {

    @Autowired
    private CanalComunicacionService canalComunicacionService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("canales", canalComunicacionService.obtenerTodos());
        return "crm/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("canal", new CanalComunicacion());
        return "crm/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute CanalComunicacion canal) {
        canalComunicacionService.guardar(canal);
        return "redirect:/crm";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("canal", canalComunicacionService.obtenerPorId(id));
        return "crm/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        canalComunicacionService.eliminar(id);
        return "redirect:/crm";
    }
}
