package com.example.web_lab4.controller;

import com.example.web_lab4.dto.AuthRequest;
import com.example.web_lab4.dto.AuthResponse;
import com.example.web_lab4.model.User;
import com.example.web_lab4.repository.UserRepository;
import com.example.web_lab4.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public String register(@Valid @RequestBody AuthRequest authRequest) {
        if(userRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            return "User already exists";
        }

        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        userRepository.save(user);

        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
            authRequest.getUsername(),
                            authRequest.getPassword()
                                    )
                                    );
} catch (BadCredentialsException e) {
        throw new RuntimeException("Неверный логин или пароль");
        }

        String token = jwtUtil.generateToken(authRequest.getUsername());
        return new AuthResponse(token);
        }
}
