package com.vet.manadawoof.config;

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

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authz -> authz
                // --- 1. ENDPOINTS PÚBLICOS ---
                .requestMatchers("/archivos/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/clientes/documento/**").permitAll()
                .requestMatchers("/api/agenda/cliente/**").permitAll()

                // --- 2. AGENDA Y SERVICIOS (Acceso para Admin y Auxiliares) ---
                .requestMatchers("/api/agenda/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR CAJA", "AUXILIAR GROMERS")
                .requestMatchers("/api/ingresos-servicios/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR CAJA", "AUXILIAR GROMERS")
                .requestMatchers("/api/pagos-agenda/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR CAJA")

                // --- 3. PERMISOS EXCLUSIVOS DE ADMINISTRADOR GENERAL ---
                .requestMatchers("/api/archivos/**").hasAuthority("ADMINISTRADOR GENERAL")
                .requestMatchers("/api/archivos-clinicos/**").hasAuthority("ADMINISTRADOR GENERAL")
                .requestMatchers("/api/asistencias/**").hasAuthority("ADMINISTRADOR GENERAL")
                .requestMatchers("/api/asignaciones-horarios/**").hasAuthority("ADMINISTRADOR GENERAL")
                .requestMatchers("/api/entidades/**").hasAuthority("ADMINISTRADOR GENERAL")
                .requestMatchers("/api/horarios-base-roles/**").hasAuthority("ADMINISTRADOR GENERAL")
                .requestMatchers("/api/proveedores/**").hasAuthority("ADMINISTRADOR GENERAL")
                .requestMatchers("/api/usuarios/**").hasAuthority("ADMINISTRADOR GENERAL")
                .requestMatchers("/api/usuarios-roles/**").hasAuthority("ADMINISTRADOR GENERAL")
                .requestMatchers("/api/roles/**").hasAuthority("ADMINISTRADOR GENERAL")
                
                // --- 4. CLIENTES Y MASCOTAS (Acceso compartido) ---
                .requestMatchers("/api/clientes/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR CAJA", "AUXILIAR GROMERS")
                .requestMatchers("/api/mascotas/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR CAJA", "AUXILIAR GROMERS")
                .requestMatchers("/api/clientes/listar_solo_con_nombreymascota").hasAnyAuthority("AUXILIAR CAJA", "ADMINISTRADOR GENERAL")

                // --- 5. ATENCIÓN MÉDICA Y VETERINARIOS ---
                .requestMatchers("/api/atenciones-medicas/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "VETERINARIO")
                .requestMatchers("/api/historia-clinica/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "VETERINARIO")
                .requestMatchers("/api/veterinarios/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "VETERINARIO", "AUXILIAR CAJA")
                .requestMatchers("/api/colaboradores/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR CAJA")

                // --- 6. TABLAS MAESTRAS (Necesarias para cargar Selects y formularios) ---
                .requestMatchers("/api/estados-agenda/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR GROMERS", "AUXILIAR CAJA")
                .requestMatchers("/api/estados-mascota/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR GROMERS", "AUXILIAR CAJA")
                .requestMatchers("/api/especialidades/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "VETERINARIO")
                .requestMatchers("/api/especies/**", "/api/razas/**", "/api/tamanos/**", "/api/etapasVida/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR CAJA")
                .requestMatchers("/api/servicios/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR CAJA", "AUXILIAR GROMERS")
                .requestMatchers("/api/medios-pago/**", "/api/medios-solicitud/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR CAJA")

                // --- 7. OTROS ENDPOINTS ---
                .requestMatchers("/api/medicamentos/**", "/api/tipoMedicamento/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "VETERINARIO")
                .requestMatchers("/api/vacunas/**", "/api/vacunas-mascota/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "VETERINARIO")
                .requestMatchers("/api/recordatorios-agenda/**", "/api/tipos-recordatorio/**").hasAnyAuthority("ADMINISTRADOR GENERAL", "AUXILIAR CAJA")
                
                // Todos los demás endpoints requieren autenticación
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
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