package com.example.useraccountservice.service.impl;

import com.example.useraccountservice.dto.AccountDTO;
import com.example.useraccountservice.dto.ApiResponse;
import com.example.useraccountservice.entity.Account;
import com.example.useraccountservice.entity.User;
import com.example.useraccountservice.enums.AccountStatus;
import com.example.useraccountservice.exceptions.NotFoundException;
import com.example.useraccountservice.repository.AccountRepository;
import com.example.useraccountservice.repository.UserRepository;
import com.example.useraccountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    public ApiResponse<AccountDTO> getMyAccount() {

        log.info("fetching account for logged in user");
        String userEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new NotFoundException("User Not Found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(()-> new NotFoundException("Account Not Found"));


        AccountDTO accountDTO = modelMapper.map(account, AccountDTO.class);

        accountDTO.setOwnerEmail(account.getUser().getEmail());

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account Retrieved",
                accountDTO
        );

    }

    @Override
    public ApiResponse<AccountDTO> getAccountNumber(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new NotFoundException("Account Not Found"));

        AccountDTO accountDTO = modelMapper.map(account, AccountDTO.class);

        accountDTO.setOwnerEmail(account.getUser().getEmail());

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account Retrieved",
                accountDTO
        );

    }

    @Override
    public ApiResponse<AccountDTO> changeAccountStatus(String accountNumber, AccountStatus status) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new NotFoundException("Account Not Found"));

        account.setAccountStatus(status);
        Account savedAccount = accountRepository.save(account);


        AccountDTO accountDTO = modelMapper.map(savedAccount, AccountDTO.class);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account Status Updated Successfully",
                accountDTO
        );
    }

    @Override
    public ApiResponse<Page<AccountDTO>> getAllAccounts(Pageable pageable) {

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );

        Page<Account> accounts = accountRepository.findAll(sortedPageable);

        Page<AccountDTO> dtoPage = accounts.map(account -> modelMapper.map(account, AccountDTO.class));

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Accounts Retrieved",
                dtoPage
        );
    }
}
