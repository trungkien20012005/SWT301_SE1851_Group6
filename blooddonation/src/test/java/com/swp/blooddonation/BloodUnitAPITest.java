package com.swp.blooddonation;

import com.swp.blooddonation.api.BloodUnitAPI;
import com.swp.blooddonation.dto.request.CollectBloodRequest;
import com.swp.blooddonation.entity.BloodUnit;
import com.swp.blooddonation.service.BloodUnitService;
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
class BloodUnitAPITest {

    @InjectMocks
    private BloodUnitAPI bloodUnitAPI;

    @Mock
    private BloodUnitService bloodUnitService;

    @Test
    void testCollectBlood_Success() {
        CollectBloodRequest request = new CollectBloodRequest();
        BloodUnit mockResult = new BloodUnit();
        Mockito.when(bloodUnitService.collectBlood(request)).thenReturn(mockResult);

        ResponseEntity<BloodUnit> result = bloodUnitAPI.collectBlood(request);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(mockResult, result.getBody());
    }
} 