package com.fintech.paymentledger.controller;

import com.fintech.paymentledger.dto.AuthResponse;
import com.fintech.paymentledger.dto.LoginRequest;
import com.fintech.paymentledger.dto.RegisterRequest;
import com.fintech.paymentledger.entity.User;
import com.fintech.paymentledger.repository.UserRepository;
import com.fintech.paymentledger.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public String register(
            @RequestBody RegisterRequest request
    ) {

        User user = new User();

        user.setUsername(request.getUsername());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole("USER");

        userRepository.save(user);

        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request
    ) {

        User user =
                userRepository
                        .findByUsername(
                                request.getUsername()
                        )
                        .orElseThrow();

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            throw new RuntimeException(
                    "Invalid username or password"
            );
        }

        String token =
                jwtUtil.generateToken(
                        user.getUsername()
                );

        return new AuthResponse(token);
    }
}