package com.swp.blooddonation;

import com.swp.blooddonation.api.BloodTestAPI;
import com.swp.blooddonation.dto.CompleteBloodTest;
import com.swp.blooddonation.dto.response.BloodTestResponse;
import com.swp.blooddonation.service.BloodTestService;
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
class BloodTestAPITest {

    @InjectMocks
    private BloodTestAPI bloodTestAPI;

    @Mock
    private BloodTestService bloodTestService;

    @Test
    void testCompleteBloodTest_Success() {
        Long id = 1L;
        CompleteBloodTest request = new CompleteBloodTest();
        BloodTestResponse mockResponse = new BloodTestResponse();
        Mockito.when(bloodTestService.completeBloodTestByRegister(id, request)).thenReturn(mockResponse);

        ResponseEntity<BloodTestResponse> result = bloodTestAPI.complete(id, request);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(mockResponse, result.getBody());
    }
} 