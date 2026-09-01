package com.example.useraccountservice.service;

import com.example.useraccountservice.dto.AccountDTO;
import com.example.useraccountservice.dto.ApiResponse;
import com.example.useraccountservice.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {

    ApiResponse<AccountDTO> getMyAccount();

    ApiResponse<AccountDTO> getAccountNumber(String accountNumber);

    ApiResponse<AccountDTO> changeAccountStatus(String accountNumber, AccountStatus status);

    ApiResponse<Page<AccountDTO>> getAllAccounts(Pageable pageable);
}