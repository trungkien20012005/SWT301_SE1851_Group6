package com.swp.blooddonation;

import com.swp.blooddonation.api.AuthenticationAPI;
import com.swp.blooddonation.dto.request.LoginRequest;
import com.swp.blooddonation.dto.request.RegisRequest;
import com.swp.blooddonation.dto.response.AccountResponse;
import com.swp.blooddonation.dto.response.RegisterAccountResponse;
import com.swp.blooddonation.service.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthenticationAPITest {

    @InjectMocks
    private AuthenticationAPI authenticationAPI;

    @Mock
    private AuthenticationService authenticationService;

    @Test
    void testLogin_Success() {
        LoginRequest loginRequest = new LoginRequest();
        AccountResponse mockResponse = new AccountResponse();
        Mockito.when(authenticationService.login(loginRequest)).thenReturn(mockResponse);

        ResponseEntity result = authenticationAPI.login(loginRequest);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(mockResponse, result.getBody());
    }

    @Test
    void testRegister_Success() {
        RegisRequest regisRequest = new RegisRequest();
        RegisterAccountResponse mockResponse = new RegisterAccountResponse();
        Mockito.when(authenticationService.register(regisRequest)).thenReturn(mockResponse);

        ResponseEntity result = authenticationAPI.register(regisRequest);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(mockResponse, result.getBody());
    }
} 