package com.vet.manadawoof.controller;

import java.io.File;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/archivos")
public class SubidArchRestController {

    private static final String BASE = "C:/Users/edgar/Desktop/archivos/"; //cambiar segun conveniencia
    private static final String MULTIMEDIA = BASE + "multimedia/";
    private static final String DOCUMENTOS = BASE + "documentos/";

    @PostMapping("/subir")
    public ResponseEntity<String> subirArchivo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nombreMascota") String nombreMascota) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo está vacío");
            }

            String originalName = file.getOriginalFilename();
            if (originalName == null || !originalName.contains(".")) {
                return ResponseEntity.badRequest().body("Archivo inválido");
            }

            // Validar tamaño
            if (file.getSize() > 10_000_000) {
                return ResponseEntity.badRequest().body("Archivo demasiado grande (máx. 10MB)");
            }

            // Extensión
            String extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();

            List<String> permitidos = List.of(".jpg", ".jpeg", ".png", ".mp4", ".pdf", ".docx");
            if (!permitidos.contains(extension)) {
                return ResponseEntity.badRequest().body("Tipo de archivo no permitido");
            }

            // Detectar multimedia
            String contentType = file.getContentType();
            boolean esMultimedia = contentType != null &&
                    (contentType.startsWith("image/") || contentType.startsWith("video/"));

            String folder = esMultimedia ? MULTIMEDIA : DOCUMENTOS;

            // Crear carpeta
            File directory = new File(folder);
            if (!directory.exists()) directory.mkdirs();

            // Limpieza del nombre
            String limpio = nombreMascota.replaceAll("[^a-zA-Z0-9_-]", "");

            // Timestamp
            String fecha = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            String nombreFinal = limpio + "_" + fecha + extension;

            File destino = new File(folder + nombreFinal);
            file.transferTo(destino);

            String publicUrl = "http://localhost:8088/archivos/" + (esMultimedia ? "multimedia/" : "documentos/") + nombreFinal;

            return ResponseEntity.ok(publicUrl);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al subir archivo: " + e.getMessage());
        }
    }

}
