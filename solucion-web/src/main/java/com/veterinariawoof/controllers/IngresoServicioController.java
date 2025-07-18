package com.veterinariawoof.controllers;

import com.veterinariawoof.models.IngresoServicio;
import com.veterinariawoof.services.IngresoServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ingresos-servicios")
public class IngresoServicioController {

    @Autowired
    private IngresoServicioService ingresoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ingresos", ingresoService.obtenerTodos());
        return "ingresos_servicios/lista";
    }

    @GetMapping("/ver/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        IngresoServicio ingreso = ingresoService.obtenerPorId(id);
        model.addAttribute("ingreso", ingreso);
        return "ingresos_servicios/detalle";
    }
}
