// package com.example.Musify.dto;

// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Pattern;
// import jakarta.validation.constraints.Size;

// public class SignupRequest {

//     @NotBlank(message = "Name is required")
//     private String name;

//     @Email(message = "Invalid email format")
//     @NotBlank(message = "Email is required")
//     private String email;

//     @NotBlank(message = "Password is required")
//     @Size(min = 8, message = "Password must be at least 8 characters long")
//     @Pattern(
//         regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
//         message = "Password must contain uppercase, lowercase, number, and special character"
//     )
//     private String password;

//     // Getters & Setters
//     public String getName() { return name; }
//     public void setName(String name) { this.name = name; }

//     public String getEmail() { return email; }
//     public void setEmail(String email) { this.email = email; }

//     public String getPassword() { return password; }
//     public void setPassword(String password) { this.password = password; }
// }
