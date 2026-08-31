package com.example.useraccountservice.service.impl;

import com.example.useraccountservice.dto.*;
import com.example.useraccountservice.entity.Account;
import com.example.useraccountservice.entity.Role;
import com.example.useraccountservice.entity.User;
import com.example.useraccountservice.enums.AccountStatus;
import com.example.useraccountservice.enums.AccountType;
import com.example.useraccountservice.enums.Currency;
import com.example.useraccountservice.kafka.dto.UserRegistrationEvent;
import com.example.useraccountservice.exceptions.BadRequestException;
import com.example.useraccountservice.exceptions.NotFoundException;
import com.example.useraccountservice.kafka.service.AccountEventPublisher;
import com.example.useraccountservice.repository.AccountRepository;
import com.example.useraccountservice.repository.RoleRepository;
import com.example.useraccountservice.repository.UserRepository;
import com.example.useraccountservice.security.JwtService;
import com.example.useraccountservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final ModelMapper modelMapper;

    private final AccountEventPublisher accountEventPublisher;

    @Override
    public ApiResponse<AuthResponse> registerUser(RegistrationRequest registrationRequest) {
        log.info("We are inside the register user service method");

        if(userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new BadRequestException("Account already exist for this email.");
        }

        Set<Role> roles = new HashSet<>();

        String roleName = (registrationRequest.getRole() != null && !registrationRequest.getRole().isBlank())
                ? registrationRequest.getRole().toUpperCase()
                : "CUSTOMER";

        Role databaseRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role with name : " + roleName + " do not exist."));

        roles.add(databaseRole);

        User userToSave = User.builder()
                .email(registrationRequest.getEmail())
                .password(registrationRequest.getPassword())
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .enabled(true)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(userToSave);

        //generate a unique account number for the uer
        String accountNumber = generateUniqueAccountNumber();

        Account accountToSaveToDb = Account.builder()
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .currency(Currency.USD)
                .accountType(AccountType.SAVINGS)
                .accountStatus(AccountStatus.ACTIVE)
                .user(savedUser)
                .build();

        accountRepository.save(accountToSaveToDb);

        //Publish event out to the notification service
        UserRegistrationEvent userRegistrationEvent = UserRegistrationEvent.builder()
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .accountNumber(accountNumber)
                .bankName("NOVA BANK")
                .build();

        accountEventPublisher.publishedUserRegistrationEvent(userRegistrationEvent);

        String token = jwtService.generateToken(savedUser.getEmail());

        UserDTO userDTO = modelMapper.map(savedUser, UserDTO.class);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();

        return new ApiResponse<>(HttpStatus.CONTINUE.value(), "User account created successfully", authResponse);
    }

    @Override
    public ApiResponse<AuthResponse> loginUser(LoginRequest loginRequest) {
        log.info("Inside login user method");

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() ->
                        new NotFoundException("User mail Id not found"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Password doesn't match");
        }

        if(!user.isEnabled()) {
            throw new BadRequestException("User is disabled");
        }

        String token = jwtService.generateToken(user.getEmail());

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "User account created successfully",
                authResponse
        );
    }

    private String generateUniqueAccountNumber(){
        String accountNumber;

        ThreadLocalRandom random = ThreadLocalRandom.current();

        do{
            //it will generate random 8 digits(between 0000000 to 99999999)
            int randomPart = random.nextInt(100_000_000);
            accountNumber = String.format("00%08d", randomPart);

        }while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}
