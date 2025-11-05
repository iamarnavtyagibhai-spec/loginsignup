// package com.example.Musify.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;

// import com.example.Musify.model.User;
// import com.example.Musify.service.SignupService;

// import java.util.HashMap;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/auth")
// @CrossOrigin(origins = "*")
// public class UserController {

//     public UserController() {
//         System.out.println("✅ UserController loaded successfully!");
//     }

//     @Autowired
//     private SignupService signupService;

//     @PostMapping("/signup")
//     public Map<String, Object> signup(@RequestBody User user) {
//         Map<String, Object> response = new HashMap<>();
//         try {
//             System.out.println("                          HIIIIIIIIIIIIIIIIIIIIIIIIIII                      ");
//             User savedUser = signupService.signup(user);
//             response.put("message", "Signup successful!");
//             response.put("user", savedUser);
//             return response;
//         } catch (RuntimeException e) {
//             response.put("error", e.getMessage());
//             return response;
//         }
//     }
// }
package com.example.Musify.controller;

import com.example.Musify.model.User;
import com.example.Musify.service.SignupService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    private final SignupService signupService;

    public UserController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public Map<String, Object> signup(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        User saved = signupService.signup(user);
        // remove password from response
        saved.setPassword(null);
        response.put("message", "Signup successful!");
        response.put("user", saved);
        return response;
    }
}
