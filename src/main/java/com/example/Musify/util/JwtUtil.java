// package com.example.Musify.util;

// import io.jsonwebtoken.*;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.stereotype.Component;

// import java.security.Key;
// import java.util.Date;

// @Component
// public class JwtUtil {

//     // Must be Base64-encoded and at least 32 characters for HS256
//     private static final String SECRET = "your256bitsecretkeyyour256bitsecretkey";
//     private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

//     // Generate signing key from the secret
//     private Key getSigningKey() {
//         byte[] keyBytes = Decoders.BASE64.decode(SECRET);
//         return Keys.hmacShaKeyFor(keyBytes);
//     }

//     // Generate a new JWT token
//     public String generateToken(String username) {
//         return Jwts.builder()
//                 .setSubject(username)
//                 .setIssuedAt(new Date())
//                 .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                 .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                 .compact();
//     }

//     // Extract username (subject) from token
//     public String getSubject(String token) {
//         return extractAllClaims(token).getSubject();
//     }

//     // Validate if the token is well-formed and not expired
//     public boolean validateToken(String token) {
//         try {
//             extractAllClaims(token); // If parsing fails, it’ll throw
//             return true;
//         } catch (JwtException | IllegalArgumentException e) {
//             System.out.println("Invalid JWT: " + e.getMessage());
//             return false;
//         }
//     }

//     // Extract claims (payload) from the token
//     private Claims extractAllClaims(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(getSigningKey()) // ✅ new correct method in jjwt 0.11.x
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//     }
// }
package com.example.Musify.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 🔐 Must be Base64-encoded and at least 32 bytes long
    // You can generate a valid one using: 
    //   Base64.getEncoder().encodeToString("your_secure_key_1234567890".getBytes());
    private static final String SECRET = "eW91cl9zZWN1cmVfa2V5XzEyMzQ1Njc4OTA="; // Base64 of "your_secure_key_1234567890"

    private static final long EXPIRATION_TIME = 86400000; // 1 day

    // Generate signing key
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate JWT token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract username
    public String getSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid JWT: " + e.getMessage());
            return false;
        }
    }

    // Extract claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
