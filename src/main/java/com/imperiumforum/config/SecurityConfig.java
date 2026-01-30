package com.imperiumforum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Публичные страницы
                        .requestMatchers(
                                "/",
                                "/register",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**"
                        ).permitAll()

                        // Защищенные страницы (только для авторизованных)
                        .requestMatchers(
                                "/profile/**",
                                "/forum/new",
                                "/topic/new",
                                "/message/**"
                        ).authenticated()

                        // Админские страницы
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Модераторские страницы
                        .requestMatchers("/moderator/**").hasAnyRole("MODERATOR", "ADMIN")

                        // Все остальные - разрешены для всех
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")           // Наша кастомная страница логина
                        .loginProcessingUrl("/login")  // URL для обработки формы
                        .defaultSuccessUrl("/profile", true)  // После логина - в профиль
                        .failureUrl("/login?error=true")      // При ошибке
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")          // Простой способ (без AntPathRequestMatcher)
                        .logoutSuccessUrl("/?logout=true")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }
}