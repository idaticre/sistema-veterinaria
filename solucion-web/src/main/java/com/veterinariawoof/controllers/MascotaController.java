package com.veterinariawoof.controllers;

import com.veterinariawoof.models.Mascota;
import com.veterinariawoof.services.ClienteService;
import com.veterinariawoof.services.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaService service;

    @Autowired
    private ClienteService clienteService;

    public MascotaController(MascotaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("mascotas", service.findAll());
        return "mascotas/lista";
    }

@GetMapping("/nueva")
public String nuevaMascota(Model model) {
    model.addAttribute("mascota", new Mascota());
    model.addAttribute("clientes", clienteService.findAll());
    return "mascotas/formulario";
}

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Mascota mascota) {
        service.save(mascota);
        return "redirect:/mascotas";
    }

    @GetMapping("/editar/{id}")
public String editar(@PathVariable Long id, Model model) {
    model.addAttribute("mascota", service.findById(id));
    model.addAttribute("clientes", clienteService.findAll());
    return "mascotas/formulario";
}

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/mascotas";
    }
}
