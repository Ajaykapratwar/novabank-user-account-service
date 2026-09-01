package com.example.useraccountservice.service;

import com.example.useraccountservice.dto.ApiResponse;
import com.example.useraccountservice.dto.UserDTO;
import com.example.useraccountservice.dto.UserStatisticsDTO;
import com.example.useraccountservice.dto.UserWithAccountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    ApiResponse<UserWithAccountDTO> getMyDetails();

    ApiResponse<UserWithAccountDTO> searchUser(String email, String accountNumber);

    ApiResponse<Page<UserDTO>> getAllUser(String roleName, Pageable pageable);

    ApiResponse<UserStatisticsDTO> getUserStatistics();

    ApiResponse<String> toggleUserStatus(Long userId);
}