package com.vet.manadawoof;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 👇 IMPORTANTE
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 🔥 ACTIVA LOS @Scheduled
public class VetManadaWoofApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetManadaWoofApplication.class, args);
    }

}