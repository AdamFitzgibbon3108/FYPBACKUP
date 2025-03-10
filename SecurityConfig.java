package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // ✅ Enables @PreAuthorize in controllers
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // ✅ Secure password hashing
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // ✅ Allow open access to login, register, and public static files
                .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                .requestMatchers("/survey", "/survey/submit", "/questions/submit").permitAll()
                
                // ✅ Restrict /admin/** routes to ADMIN role
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // ✅ Allow authenticated users access to user-related operations
                .requestMatchers("/api/users/**").authenticated()

                // ✅ All other routes require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")  // ✅ Custom login page
                .defaultSuccessUrl("/dashboard", true)  // ✅ Redirect after successful login
                .failureUrl("/login?error=true")  // ✅ Handle login failure
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")  // ✅ Define logout endpoint
                .logoutSuccessUrl("/login?logout=true")  // ✅ Redirect after logout
                .permitAll()
            )
            .csrf().disable(); // ❌ Disabled for now (Enable in production)

        return http.build();
    }
}
