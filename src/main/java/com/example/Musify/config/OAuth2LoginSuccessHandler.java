// package com.example.Musify.config;

// import com.example.Musify.service.UserService;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
// import org.springframework.security.oauth2.core.user.OAuth2User;
// import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
// import org.springframework.stereotype.Component;

// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import java.io.IOException;
// import java.util.Map;

// @Component
// public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

//     private final UserService userService;

//     public OAuth2LoginSuccessHandler(UserService userService) {
//         this.userService = userService;
//     }

//     @Override
//     public void onAuthenticationSuccess(
//             HttpServletRequest request,
//             HttpServletResponse response,
//             Authentication authentication) throws IOException, ServletException {

//         if (authentication instanceof OAuth2AuthenticationToken token) {
//             OAuth2User user = token.getPrincipal();
//             Map<String, Object> attributes = user.getAttributes();

//             String provider = token.getAuthorizedClientRegistrationId(); // google, github, etc.
//             String providerId = (String) attributes.get("sub"); // Google user ID
//             String email = (String) attributes.get("email");
//             String name = (String) attributes.get("name");

//             // ✅ Save or update the user in MongoDB
//             userService.upsert(provider, providerId, email, name);
//         }

//         // Redirect to your frontend or profile endpoint
//         response.sendRedirect("/api/auth/me");
//     }
// }
package com.example.Musify.config;

import com.example.Musify.service.UserService;
import com.example.Musify.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public OAuth2LoginSuccessHandler(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        if (authentication instanceof OAuth2AuthenticationToken token) {
            OAuth2User user = token.getPrincipal();
            Map<String, Object> attributes = user.getAttributes();

            String provider = token.getAuthorizedClientRegistrationId();
            String providerId = (String) attributes.get("sub");
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");

            userService.upsert(provider, providerId, email, name);

            // generate JWT and return to client
            String jwt = jwtUtil.generateToken(email);

            // For browser flows you might redirect to frontend with token as query param:
            // response.sendRedirect("https://your-frontend.example.com/oauth-success?token=" + jwt);

            // For Postman/testing return token as simple JSON
            response.setContentType("application/json");
            response.getWriter().write("{\"token\":\"" + jwt + "\"}");
            return;
        }

        response.sendRedirect("/api/public/hello");
    }
}
