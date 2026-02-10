package com.netspecter.controller;

import com.netspecter.dto.AuthenticationRequest;
import com.netspecter.dto.RegisterRequest;
import com.netspecter.model.User;
import com.netspecter.repository.UserRepository;
import com.netspecter.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BindingResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Operations related to user registration and login")
public class AuthController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${application.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Operation(summary = "Register a new user", description = "Creates a new account with email and password. Returns a JWT token upon success.")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(errors));
        }

        if (repository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(
                            "Email already registered. Please use a different email or try logging in."));
        }

        var user = new User();
        user.setUsername(request.getEmail()); // Using email as username
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER);
        repository.save(user);

        var jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), java.util.Collections.emptyList()));

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .build());
    }

    @Operation(summary = "Authenticate user", description = "Login with email and password to receive a JWT token.")
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
            var user = repository.findByEmail(request.getEmail())
                    .orElseThrow();
            var jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                    user.getEmail(), user.getPassword(), java.util.Collections.emptyList()));
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .token(jwtToken)
                    .email(user.getEmail())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid email or password. Please check your credentials and try again."));
        }
    }

    @Operation(summary = "Forgot password", description = "Initiate password reset process for the given email.")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        var userOpt = repository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(new MessageResponse(
                    "If an account exists with this email, you will receive password reset instructions."));
        }
        // TODO: In production, send email with reset link
        // For now, just confirm the email exists
        return ResponseEntity.ok(new MessageResponse(
                "Password reset instructions have been sent to " + request.getEmail()));
    }

    // OAuth2 Redirect Endpoint
    @GetMapping("/oauth2/success")
    public void oauth2Success(Authentication authentication, HttpServletResponse response) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = repository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(email);
            newUser.setPassword(passwordEncoder.encode("OAUTH2_PlaceHolder")); // Dummy password
            newUser.setRole(User.Role.USER);
            return repository.save(newUser);
        });

        String jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), java.util.Collections.emptyList()));

        // Redirect to Frontend with Token
        response.sendRedirect(frontendUrl + "/oauth/callback?token=" + jwtToken);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthenticationResponse {
        private String token;
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorResponse {
        private String error;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageResponse {
        private String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ForgotPasswordRequest {
        private String email;
    }
}
