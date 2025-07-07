package com.veterinariawoof.controllers;

import com.veterinariawoof.dto.CitaDTO;
import com.veterinariawoof.services.CitaService;
import com.veterinariawoof.repositories.ClienteRepository;
import com.veterinariawoof.repositories.MascotaRepository;
import com.veterinariawoof.repositories.TipoServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private TipoServicioRepository tipoServicioRepository;

    @GetMapping
    public String listarCitas(Model model) {
        model.addAttribute("citas", citaService.listarTodas());
        return "citas/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("citaDTO", new CitaDTO());
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("mascotas", mascotaRepository.findAll());
        model.addAttribute("servicios", tipoServicioRepository.findAll());
        return "citas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarCita(@ModelAttribute CitaDTO citaDTO) {
        citaService.guardar(citaDTO);
        return "redirect:/citas";
    }
}
