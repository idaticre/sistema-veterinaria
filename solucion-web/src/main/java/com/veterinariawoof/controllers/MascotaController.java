package com.veterinariawoof.controllers;

import com.veterinariawoof.models.Mascota;
import com.veterinariawoof.services.MascotaService;
import com.veterinariawoof.services.ClienteService;
import com.veterinariawoof.services.EspecieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EspecieService especieService; // ✅ Nuevo

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("mascotas", mascotaService.obtenerTodas());
        return "mascotas/lista";
    }

    @GetMapping("/crear")
    public String crearFormulario(Model model) {
        model.addAttribute("mascota", new Mascota());
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("especies", especieService.obtenerTodas()); // ✅ Agregado
        return "mascotas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Mascota mascota) {
        mascotaService.guardar(mascota);
        return "redirect:/mascotas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("mascota", mascotaService.obtenerPorId(id));
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("especies", especieService.obtenerTodas()); // ✅ Agregado también en edición
        return "mascotas/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        mascotaService.eliminar(id);
        return "redirect:/mascotas";
    }
}
