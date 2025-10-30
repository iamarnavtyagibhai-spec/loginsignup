// package com.example.Musify.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// public class SecurityConfig 
//     private final OAuth2LoginSuccessHandler successHandler;

//     public SecurityConfig(OAuth2LoginSuccessHandler successHandler) {
//         this.successHandler = successHandler;
//     }

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//             .csrf(csrf -> csrf.disable())
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/", "/api/public/**", "/api/auth/**").permitAll()
//                 .anyRequest().authenticated()
//             )
//             .oauth2Login(oauth2 -> oauth2.successHandler(successHandler))
//             .logout(logout -> logout.logoutSuccessUrl("/api/public/logout-success").permitAll());
//         System.out.println("                          HEEEEEEEEEELLLLLLLLLLLLLLOOOOOOOOOOOOOOOO                     ");
//         return http.build();
//     }
// }
package com.example.Musify.config;

import com.example.Musify.security.JwtAuthenticationFilter;
import com.example.Musify.service.TokenBlacklistService;
import com.example.Musify.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler successHandler;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;

    public SecurityConfig(OAuth2LoginSuccessHandler successHandler, JwtUtil jwtUtil, TokenBlacklistService blacklistService) {
        this.successHandler = successHandler;
        this.jwtUtil = jwtUtil;
        this.blacklistService = blacklistService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil, blacklistService);

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", 
                    "/api/public/**", 
                    "/api/auth/**",
                    "/oauth2/**" // allow oauth2 endpoints
                ).permitAll()
                .anyRequest().authenticated()
            )

            .oauth2Login(oauth2 -> oauth2
                .successHandler(successHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/api/public/logout-success")
                .permitAll()
            );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}



