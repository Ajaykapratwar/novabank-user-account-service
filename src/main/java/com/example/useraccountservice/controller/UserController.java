package com.example.useraccountservice.controller;

import com.example.useraccountservice.dto.ApiResponse;
import com.example.useraccountservice.dto.UserWithAccountDTO;
import com.example.useraccountservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserWithAccountDTO>> getMyProfile(){
        return ResponseEntity.ok(userService.getMyDetails());
    }
}