
package com.vet.manadawoof.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
<<<<<<< HEAD

                        // Endpoints que requieren roles específicos

                        //Permisos de solo Admin
                        .requestMatchers("/api/asignaciones/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/colaboradores/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/dias/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/empresas/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/entidades/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/estado-asistencia/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/horarios-trabajo/**").hasAuthority("ADMINISTRADOR GENERAL")

                        //Permisos del Auxiliar Caja

                        .requestMatchers(HttpMethod.POST,"/api/clientes/**").hasAnyAuthority("AUXILIAR CAJA","ADMINISTRADOR GENERAL")
                        .requestMatchers(HttpMethod.POST,"/api/mascotas/**").hasAnyAuthority("ADMINISTRADOR GENERAL","AUXILIAR CAJA")
                        // (pendiente por hacer)
                        .requestMatchers("/api/clientes/listar_solo_con_nombreymascota").hasAnyAuthority("AUXILIAR CAJA","ADMINISTRADOR GENERAL")
                        // Permisos del Auxiliar Gromers

=======
                        .requestMatchers("/api/public/**").permitAll()
                        
                        // Endpoints que requieren roles específicos
                        .requestMatchers("/api/por_definir/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/por_definir/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "VETERINARIO")
                        .requestMatchers("/api/por_definir/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR CAJA")

                        .requestMatchers(HttpMethod.POST,"/api/clientes/**").hasAnyAuthority("AUXILIAR CAJA", "AUXILIAR GROMERS")
>>>>>>> f8392b9df396ad876a129fdadc95d0dc016576a9
                        .requestMatchers(HttpMethod.GET,"/api/clientes/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR GROMERS")
                        .requestMatchers("/api/estados-agenda/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR GROMERS", "AUXILIAR CAJA")
                        .requestMatchers("/api/estados-mascota/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR GROMERS", "AUXILIAR CAJA")
                        //.requestMatchers(HttpMethod.GET,"/api/mascotas/**").hasAnyAuthority("ADMINISTRADOR GENERAL","AUXILIAR GROMERS")


<<<<<<< HEAD
                        // Permisos que todavia no han sido definido quienes seran los responsables
                        .requestMatchers("/api/canales-comunicacion/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/especialidades/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/especies/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/etapasVida/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/medicamentos-mascota/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/medicamentos/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/medios-pago/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/medios-solicitud/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/proveedores/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/razas/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/asistencias/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/roles/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/servicios/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/tamanos/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/tipo-documento/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/tipoMedicamento/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/tipo-persona-juridica/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/tipos-recordatorio/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/usuarios/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/usuarios-roles/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/vacunas-mascota/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/vacunas/**").hasAuthority("ADMINISTRADOR GENERAL")
                        .requestMatchers("/api/veterinarios/**").hasAuthority("ADMINISTRADOR GENERAL")

=======

                        .requestMatchers(HttpMethod.POST, "/api/clientes/**").hasAnyAuthority("AUXILIAR CAJA")
                        .requestMatchers(HttpMethod.GET, "/api/clientes/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR GROMERS")
                        .requestMatchers("/api/colaboradores/**").hasAnyAuthority("ADMINISTRADOR GENERAL")
                        
                        

                        // Todos los demás endpoints requieren autenticación
>>>>>>> f8392b9df396ad876a129fdadc95d0dc016576a9
                        .anyRequest().authenticated()
                );
        
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

