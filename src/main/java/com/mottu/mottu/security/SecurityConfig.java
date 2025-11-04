package com.mottu.mottu.security;

import com.mottu.mottu.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 🔓 Recursos estáticos e páginas públicas
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/fonts/**",
                                "/pages/**",
                                "/fragments/**",
                                "/login",
                                "/error"
                        ).permitAll()

                        // Endpoints REST liberados (para testes no Postman)
                        .requestMatchers(
                                "/usuarios/**",
                                "/galpoes/**",
                                "/motos/**",
                                "/motoqueiros/**",
                                "/manutencoes/**"
                        ).permitAll()

                        // GET — qualquer usuário autenticado pode visualizar
                        .requestMatchers(HttpMethod.GET,
                                "/galpoes-view/**",
                                "/manutencoes-view/**",
                                "/motos-view/**",
                                "/motoqueiros-view/**"
                        ).authenticated()

                        // Somente ADMIN pode criar, editar ou excluir
                        .requestMatchers(HttpMethod.POST,
                                "/galpoes-view/**",
                                "/manutencoes-view/**",
                                "/motos-view/**",
                                "/motoqueiros-view/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/galpoes-view/**",
                                "/manutencoes-view/**",
                                "/motos-view/**",
                                "/motoqueiros-view/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/galpoes-view/**",
                                "/manutencoes-view/**",
                                "/motos-view/**",
                                "/motoqueiros-view/**"
                        ).hasRole("ADMIN")

                        // Todo o resto precisa apenas estar logado
                        .anyRequest().authenticated()
                )

                // Login
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )

                // Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )

                // CSRF desativado para testes
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
