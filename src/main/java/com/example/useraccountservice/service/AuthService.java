package com.example.useraccountservice.service;

import com.example.useraccountservice.dto.ApiResponse;
import com.example.useraccountservice.dto.AuthResponse;
import com.example.useraccountservice.dto.LoginRequest;
import com.example.useraccountservice.dto.RegistrationRequest;

public interface AuthService {

    ApiResponse<AuthResponse> registerUser(RegistrationRequest registrationRequest);

    ApiResponse<AuthResponse> loginUser(LoginRequest loginRequest);
}
