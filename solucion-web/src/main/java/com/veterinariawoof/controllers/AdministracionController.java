package com.veterinariawoof.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdministracionController {
    
    @GetMapping("/administracion")
    public String listar() {
        return "/administracion/administracion";
    }
}
