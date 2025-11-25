
package com.vet.manadawoof;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("admin_woof (admin123): " + encoder.encode("admin123"));
        System.out.println("admin_g2 (admin234): " + encoder.encode("admin234"));
        System.out.println("caja_milo (caja123): " + encoder.encode("caja123"));
        System.out.println("gromer_luna (luna123): " + encoder.encode("luna123"));
    }
}
