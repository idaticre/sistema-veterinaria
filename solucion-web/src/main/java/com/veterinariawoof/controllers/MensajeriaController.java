package com.veterinariawoof.controllers;

import com.veterinariawoof.models.Mensaje;
import com.veterinariawoof.services.MensajeriaService;
import com.veterinariawoof.services.ClienteService;
import com.veterinariawoof.services.CanalComunicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mensajeria")
public class MensajeriaController {

    @Autowired
    private MensajeriaService mensajeriaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CanalComunicacionService canalComunicacionService;

    @GetMapping
    public String listarMensajes(Model model) {
        model.addAttribute("mensajes", mensajeriaService.obtenerTodos());
        return "mensajeria/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoMensaje(Model model) {
        model.addAttribute("mensaje", new Mensaje());
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("canales", canalComunicacionService.obtenerTodos());
        return "mensajeria/formulario";
    }

    @PostMapping("/guardar")
    public String guardarMensaje(@ModelAttribute Mensaje mensaje) {
        mensajeriaService.guardar(mensaje);
        return "redirect:/mensajeria";
    }

    @GetMapping("/editar/{id}")
    public String editarMensaje(@PathVariable Long id, Model model) {
        Mensaje mensaje = mensajeriaService.obtenerPorId(id);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("canales", canalComunicacionService.obtenerTodos());
        return "mensajeria/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMensaje(@PathVariable Long id) {
        mensajeriaService.eliminar(id);
        return "redirect:/mensajeria";
    }
}
