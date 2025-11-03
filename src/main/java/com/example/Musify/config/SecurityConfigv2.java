// package com.example.Musify.config;

// import com.example.Musify.security.JwtAuthenticationFilter;
// import com.example.Musify.service.TokenBlacklistService;
// import com.example.Musify.util.JwtUtil;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration
// public class SecurityConfig {

//     private final OAuth2LoginSuccessHandler successHandler;
//     private final JwtUtil jwtUtil;
//     private final TokenBlacklistService blacklistService;

//     public SecurityConfig(OAuth2LoginSuccessHandler successHandler, JwtUtil jwtUtil, TokenBlacklistService blacklistService) {
//         this.successHandler = successHandler;
//         this.jwtUtil = jwtUtil;
//         this.blacklistService = blacklistService;
//     }

//     @Bean
//     public BCryptPasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         // JWT filter
//         JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil, blacklistService);

//         http
//             // Disable CSRF for APIs
//             .csrf(csrf -> csrf.disable())

//             // Stateless session management
//             .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

//             // Endpoint security
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers(
//                     "/", 
//                     "/api/public/**",
//                     "/api/auth/**",
//                     "/oauth2/**"
//                 ).permitAll()
//                 .anyRequest().authenticated()
//             )

//             // OAuth2 login
//             .oauth2Login(oauth2 -> oauth2
//                 .successHandler(successHandler)
//             )

//             // Logout
//             .logout(logout -> logout
//                 .logoutUrl("/api/auth/logout")
//                 .logoutSuccessUrl("/api/public/logout-success")
//                 .permitAll()
//             );

//         // Add JWT filter before Spring Security's UsernamePasswordAuthenticationFilter
//         http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }
// }
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
                    "/api/public/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            // ✅ Enable Google OAuth2 login
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2LoginSuccessHandler)
            );

        // ✅ Enable JWT filter for API authentication
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
