package com.swp.blooddonation;

import com.swp.blooddonation.dto.request.LoginRequest;
import com.swp.blooddonation.dto.response.AccountResponse;
import com.swp.blooddonation.entity.Account;
import com.swp.blooddonation.enums.EnableStatus;
import com.swp.blooddonation.enums.Role;
import com.swp.blooddonation.repository.AuthenticationReponsitory;
import com.swp.blooddonation.service.AuthenticationService;
import com.swp.blooddonation.service.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @InjectMocks
    private AuthenticationService authenticationService;
    @Mock
    private AuthenticationReponsitory authenticationReponsitory;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private TokenService tokenService;

    @Test
    void testLogin_Customer_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("customer2@gmail.com");
        request.setPassword("customer2");
        Account account = new Account();
        account.setEmail("customer2@gmail.com");
        account.setRole(Role.CUSTOMER);
        account.setEnableStatus(EnableStatus.ENABLE);
        account.setPassword("encodedPassword");
        Mockito.when(authenticationReponsitory.findAccountByEmail(request.getEmail())).thenReturn(account);
        Mockito.when(passwordEncoder.matches(request.getPassword(), account.getPassword())).thenReturn(true);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mockito.mock(Authentication.class));
        AccountResponse mockResponse = new AccountResponse();
        mockResponse.setRole(Role.CUSTOMER);
        Mockito.when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(mockResponse);
        Mockito.when(tokenService.generateToken(Mockito.any())).thenReturn("mocked-token");
        AccountResponse response = authenticationService.login(request);
        Assertions.assertEquals(Role.CUSTOMER, response.getRole());
    }

    @Test
    void testLogin_MedicalStaff_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ms@example.com");
        request.setPassword("password");
        Account account = new Account();
        account.setEmail("ms@example.com");
        account.setRole(Role.MEDICALSTAFF);
        account.setEnableStatus(EnableStatus.ENABLE);
        account.setPassword("encodedPassword");
        Mockito.when(authenticationReponsitory.findAccountByEmail(request.getEmail())).thenReturn(account);
        Mockito.when(passwordEncoder.matches(request.getPassword(), account.getPassword())).thenReturn(true);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mockito.mock(Authentication.class));
        AccountResponse mockResponse = new AccountResponse();
        mockResponse.setRole(Role.MEDICALSTAFF);
        Mockito.when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(mockResponse);
        Mockito.when(tokenService.generateToken(Mockito.any())).thenReturn("mocked-token");
        AccountResponse response = authenticationService.login(request);
        Assertions.assertEquals(Role.MEDICALSTAFF, response.getRole());
    }

    @Test
    void testLogin_Manager_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("manager@example.com");
        request.setPassword("password");
        Account account = new Account();
        account.setEmail("manager@example.com");
        account.setRole(Role.MANAGER);
        account.setEnableStatus(EnableStatus.ENABLE);
        account.setPassword("encodedPassword");
        Mockito.when(authenticationReponsitory.findAccountByEmail(request.getEmail())).thenReturn(account);
        Mockito.when(passwordEncoder.matches(request.getPassword(), account.getPassword())).thenReturn(true);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mockito.mock(Authentication.class));
        AccountResponse mockResponse = new AccountResponse();
        mockResponse.setRole(Role.MANAGER);
        Mockito.when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(mockResponse);
        Mockito.when(tokenService.generateToken(Mockito.any())).thenReturn("mocked-token");
        AccountResponse response = authenticationService.login(request);
        Assertions.assertEquals(Role.MANAGER, response.getRole());
    }

    @Test
    void testLogin_Fail_InvalidEmail() {
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@example.com");
        request.setPassword("password");
        Mockito.when(authenticationReponsitory.findAccountByEmail(request.getEmail())).thenReturn(null);
        Assertions.assertThrows(RuntimeException.class, () -> authenticationService.login(request));
    }
} 