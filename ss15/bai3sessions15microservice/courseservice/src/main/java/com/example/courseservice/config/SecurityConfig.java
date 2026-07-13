package com.example.courseservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // BẬT @PreAuthorize cho Spring Boot 3.x
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Cú pháp mới cho Spring Boot 3.x
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new RoleHeaderFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Filter đọc role từ Header X-User-Role
     */
    public static class RoleHeaderFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {

            // Log để debug
            System.out.println("=== DEBUG: Headers received ===");
            java.util.Collections.list(request.getHeaderNames())
                    .forEach(headerName -> {
                        System.out.println(headerName + ": " + request.getHeader(headerName));
                    });

            // Lấy role từ header
            String role = request.getHeader("X-User-Role");
            System.out.println("Role from header: " + role);

            if (role != null && !role.isEmpty()) {
                // Tạo authentication object với role từ header
                RoleBasedAuthentication authentication = new RoleBasedAuthentication(role);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        }
    }

    /**
     * Custom Authentication implementation cho Spring Boot 3.x
     */
    public static class RoleBasedAuthentication implements org.springframework.security.core.Authentication {
        private final String role;
        private boolean authenticated = true;

        public RoleBasedAuthentication(String role) {
            this.role = role;
        }

        @Override
        public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
            return Collections.singletonList(new SimpleGrantedAuthority(role));
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return role;
        }

        @Override
        public boolean isAuthenticated() {
            return authenticated;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            this.authenticated = isAuthenticated;
        }

        @Override
        public String getName() {
            return role;
        }
    }
}