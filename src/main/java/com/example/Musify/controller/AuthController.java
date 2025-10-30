// package com.example.Musify.controller;

// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.oauth2.core.user.OAuth2User;
// import org.springframework.web.bind.annotation.*;

// import java.util.HashMap;
// import java.util.Map;

// @RestController
// @CrossOrigin(origins = "*")
// public class AuthController {

//     // Public endpoint to check server status
//     @GetMapping("/api/public/hello")
//     public Map<String, Object> hello() {
//         Map<String, Object> response = new HashMap<>();
//         response.put("message", "Backend is running. Use /oauth2/authorization/google to login.");
//         return response;
//     }

//     // OAuth2 endpoint to get logged-in user info
//     @GetMapping("/api/auth/me")
//     public Map<String, Object> getOAuth2User(@AuthenticationPrincipal OAuth2User principal) {
//         Map<String, Object> response = new HashMap<>();

//         if (principal == null) {
//             response.put("error", "Not authenticated");
//             return response;
//         }

//         response.put("name", principal.getAttribute("name"));
//         response.put("email", principal.getAttribute("email"));
//         response.put("picture", principal.getAttribute("picture"));
//         response.put("providerId", principal.getAttribute("sub"));
//         response.put("message", "User authenticated successfully");

//         return response;
//     }
// }
package com.example.Musify.controller;

import com.example.Musify.model.User;
import com.example.Musify.repository.UserRepository;
import com.example.Musify.service.TokenBlacklistService;
import com.example.Musify.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;

    public AuthController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil, TokenBlacklistService blacklistService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.blacklistService = blacklistService;
    }

    // Manual login -> returns { token: "..." }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String emailOrUsername = body.get("email"); // you can accept username too
        String password = body.get("password");
        if (emailOrUsername == null || password == null) {
            throw new BadCredentialsException("Missing credentials");
        }

        User user = userRepository.findByEmail(emailOrUsername)
                .orElseGet(() -> userRepository.findByUsername(emailOrUsername).orElse(null));

        if (user == null || user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        return ResponseEntity.ok(resp);
    }

    // Logout: blacklist token (sent in Authorization header)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            blacklistService.blacklist(token);
        }
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    // Return info for current user (works for both OAuth and JWT flows)
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal(expression = "attributes") Map<String, Object> oauthAttrs, Authentication authentication) {
        // If JWT flow: Authentication.getPrincipal() contains subject (we set subject to email)
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            String subject = (String) authentication.getPrincipal();
            User user = userRepository.findByEmail(subject).orElse(null);
            if (user != null) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("id", user.getId());
                resp.put("name", user.getName());
                resp.put("email", user.getEmail());
                resp.put("username", user.getUsername());
                resp.put("provider", user.getProvider());
                resp.put("providerId", user.getProviderId());
                return ResponseEntity.ok(resp);
            }
        }

        // If OAuth flow, @AuthenticationPrincipal OAuth2User attributes
        if (oauthAttrs != null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("name", oauthAttrs.get("name"));
            resp.put("email", oauthAttrs.get("email"));
            resp.put("picture", oauthAttrs.get("picture"));
            resp.put("providerId", oauthAttrs.get("sub"));
            return ResponseEntity.ok(resp);
        }

        return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
    }
}

