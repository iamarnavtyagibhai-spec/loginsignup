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
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          TokenBlacklistService blacklistService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.blacklistService = blacklistService;
    }

    // LOGIN → returns JWT token
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String username = body.get("username");
        String password = body.get("password");

        if ((email == null || email.isBlank()) && (username == null || username.isBlank()) || password == null || password.isBlank()) {
            return ResponseEntity.status(400).body(Map.of(
                    "error", "Provide email or username, and password"
            ));
        }

        User user = null;
        if (email != null && !email.isBlank()) {
            user = userRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
        } else if (username != null && !username.isBlank()) {
            user = userRepository.findByUsername(username.trim()).orElse(null);
        }

        if (user == null || user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Invalid credentials"
            ));
        }

        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Login successful");
        resp.put("token", token);
        return ResponseEntity.ok(resp);
    }

    // LOGOUT → blacklist token
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            blacklistService.blacklist(token);
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // AUTHENTICATED USER INFO (JWT or OAuth)
    @GetMapping("/me")
    public ResponseEntity<?> me(
            @AuthenticationPrincipal(expression = "attributes") Map<String, Object> oauthAttrs,
            Authentication authentication) {

        // For JWT
        if (authentication != null && authentication.getPrincipal() instanceof String email) {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("id", user.getId());
                resp.put("name", user.getName());
                resp.put("email", user.getEmail());
                resp.put("username", user.getUsername());
                return ResponseEntity.ok(resp);
            }
        }

        // For OAuth2 (if used later)
        if (oauthAttrs != null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("name", oauthAttrs.get("name"));
            resp.put("email", oauthAttrs.get("email"));
            resp.put("picture", oauthAttrs.get("picture"));
            return ResponseEntity.ok(resp);
        }

        return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
    }
}
