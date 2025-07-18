package com.veterinariawoof.controllers;

import com.veterinariawoof.models.Agenda;
import com.veterinariawoof.services.AgendaService;
import com.veterinariawoof.services.ClienteService;
import com.veterinariawoof.services.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/agenda")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("citas", agendaService.obtenerTodas());
        return "agenda/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("cita", new Agenda());
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("mascotas", mascotaService.obtenerTodas());
        return "agenda/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Agenda cita = agendaService.obtenerPorId(id);
        model.addAttribute("cita", cita);
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("mascotas", mascotaService.obtenerTodas());
        return "agenda/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("cita") Agenda cita) {
        agendaService.guardar(cita);
        return "redirect:/agenda";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        agendaService.eliminar(id);
        return "redirect:/agenda";
    }
}
