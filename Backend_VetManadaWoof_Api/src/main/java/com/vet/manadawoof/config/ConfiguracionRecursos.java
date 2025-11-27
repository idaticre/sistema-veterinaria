package com.vet.manadawoof.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfiguracionRecursos implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers (ResourceHandlerRegistry registro){
        registro.addResourceHandler("/archivos/**").addResourceLocations("file:C:/Users/edgar/Desktop/archivos/");//direccion cambiar segun convenicia
    }
    
}
