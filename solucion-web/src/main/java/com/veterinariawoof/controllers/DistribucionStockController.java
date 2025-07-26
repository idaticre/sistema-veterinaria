package com.veterinariawoof.controllers;

import com.veterinariawoof.models.Inventario;
import com.veterinariawoof.services.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DistribucionStockController {
    
    @Autowired
    private InventarioService inventarioService;

    @GetMapping("/distribucion_y_stock")
    public String mostrarInventario(Model model) {
        List<Inventario> lista = inventarioService.listar();
        model.addAttribute("inventarios", lista);
        return "distribucion_y_stock/lista";
    }
}