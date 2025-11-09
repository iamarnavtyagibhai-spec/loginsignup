package com.example.Musify.config;

import com.example.Musify.security.JwtAuthenticationFilter;
import com.example.Musify.service.TokenBlacklistService;
import com.example.Musify.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfigv2 {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfigv2(JwtUtil jwtUtil,
                            TokenBlacklistService blacklistService,
                            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.jwtUtil = jwtUtil;
        this.blacklistService = blacklistService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, blacklistService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/signup",
                    "/oauth2/**",
                    "/api/public/**",
                    "/api/songs",
                    "/api/songs/**",      // ✅ Songs list + search allowed
                    "/api/songs/file/**",
                    "/images/**"          // ✅ Image download allowed
                ).permitAll()
                .requestMatchers(
                    "/api/songs/upload"   // ✅ Only upload requires token
                ).authenticated()
                .anyRequest().permitAll()
            )
            .exceptionHandling(e -> e.authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(401);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Unauthorized\"}");
            }))
            .oauth2Login(oauth -> oauth.successHandler(oAuth2LoginSuccessHandler));

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

