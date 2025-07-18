package com.veterinariawoof.controllers;

import com.veterinariawoof.models.Servicio;
import com.veterinariawoof.services.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("servicios", servicioService.obtenerTodos());
        return "servicios/lista";
    }

    @GetMapping("/crear")
    public String crearFormulario(Model model) {
        model.addAttribute("servicio", new Servicio());
        return "servicios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Servicio servicio) {
        servicioService.guardar(servicio);
        return "redirect:/servicios";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("servicio", servicioService.obtenerPorId(id));
        return "servicios/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        servicioService.eliminar(id);
        return "redirect:/servicios";
    }
}
