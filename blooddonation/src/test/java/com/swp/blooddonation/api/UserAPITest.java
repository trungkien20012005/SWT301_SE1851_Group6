package com.swp.blooddonation.api;

import com.swp.blooddonation.dto.CustomerDTO;
import com.swp.blooddonation.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAPITest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserAPI userAPI;

    public UserAPITest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProfile() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1L);
        dto.setFullName("Nguyen Van A");
        when(userService.getProfile()).thenReturn(dto);

        ResponseEntity<CustomerDTO> response = userAPI.getProfile();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Nguyen Van A", response.getBody().getFullName());
    }
} 