package com.example.useraccountservice.controller;

import com.example.useraccountservice.dto.ApiResponse;
import com.example.useraccountservice.dto.AuthResponse;
import com.example.useraccountservice.dto.LoginRequest;
import com.example.useraccountservice.dto.RegistrationRequest;
import com.example.useraccountservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody @Valid RegistrationRequest registrationRequest){
        return ResponseEntity.ok(authService.registerUser(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(authService.loginUser(loginRequest));
    }

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<?>> hello(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(new ApiResponse<>(200, "Success", "Welcome To Nova Bank"));
    }
}