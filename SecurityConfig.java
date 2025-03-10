package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity // ✅ Enables @PreAuthorize in controllers for role-based access
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // ✅ Secure password hashing
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // ✅ Allow public access to login, register, and static resources (CSS, JS, etc.)
                .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                
                // ✅ Allow unrestricted access to survey endpoints
                .requestMatchers("/survey", "/survey/submit", "/questions/submit").permitAll()

                // ✅ Restrict /admin/** routes to only ADMIN users
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // ✅ Allow authenticated users to access user-related API operations
                .requestMatchers("/api/users/**").authenticated()

                // ✅ Restrict all other endpoints to authenticated users
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
            .sessionManagement(session -> session
                .sessionFixation().migrateSession() // ✅ Prevent session fixation attacks
                .maximumSessions(1) // ✅ Allow only one active session per user
                .expiredUrl("/login?expired=true") // ✅ Redirect if session expires
            )
            .csrf().disable(); // ❌ Disabled for now (Enable in production)

        return http.build();
    }
}

