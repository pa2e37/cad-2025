package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
    //метод с методички устарел пришлось искать и думать новый и вот че придумал(круто же я молодец <- я молодец придумала вскод как автодополнение)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder.encode("password"))
            .roles("USER")
            .build();

        UserDetails moderator = User.builder()
            .username("moderator")
            .password(passwordEncoder.encode("password"))
            .roles("MODERATOR")
            .build();

        UserDetails bibliotecar = User.builder()
            .username("bibliotecar")
            .password(passwordEncoder.encode("password"))
            .roles("BIBLIOTECAR")
            .build();

        return new InMemoryUserDetailsManager(user, moderator, bibliotecar);
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/addBook", "/deleteBook/**").hasRole("MODERATOR")
                .requestMatchers("/updateBook/**").hasRole("BIBLIOTECAR") // задание ток библиотекарь может менять доступность книги!
                .requestMatchers("/readBook/**").hasAnyRole("USER", "MODERATOR", "BIBLIOTECAR")
                .requestMatchers("/available").hasAnyRole("USER", "MODERATOR", "BIBLIOTECAR")
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .logout(Customizer.withDefaults());

        return http.build();
    }
}