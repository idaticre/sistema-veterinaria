package com.veterinariawoof.controllers;

import com.veterinariawoof.models.dto.VentaDTO;
import com.veterinariawoof.repositories.ClienteRepository;
import com.veterinariawoof.repositories.IngresoServicioRepository;
import com.veterinariawoof.repositories.MedioPagoRepository;
import com.veterinariawoof.repositories.ProductoRepository;
import com.veterinariawoof.services.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ProductoRepository productoRepository;
    private final IngresoServicioRepository ingresoServicioRepository;
    private final ClienteRepository clienteRepository;
    private final MedioPagoRepository medioPagoRepository;

    public VentaController(VentaService ventaService,
                           ProductoRepository productoRepository,
                           IngresoServicioRepository ingresoServicioRepository,
                           ClienteRepository clienteRepository,
                           MedioPagoRepository medioPagoRepository) {
        this.ventaService = ventaService;
        this.productoRepository = productoRepository;
        this.ingresoServicioRepository = ingresoServicioRepository;
        this.clienteRepository = clienteRepository;
        this.medioPagoRepository = medioPagoRepository;
    }

    @GetMapping
    public String verVentas(Model model) {
        // Puedes cargar ventas si deseas
        return "ventas/lista";
    }

    @GetMapping("/nueva")
    public String nuevaVentaForm(Model model) {
        model.addAttribute("ventaDTO", new VentaDTO());
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("servicios", ingresoServicioRepository.findAll());
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("mediosPago", medioPagoRepository.findAll());
        return "ventas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarVenta(@ModelAttribute VentaDTO ventaDTO) {
        ventaService.registrarVenta(ventaDTO);
        return "redirect:/ventas";
    }
}
