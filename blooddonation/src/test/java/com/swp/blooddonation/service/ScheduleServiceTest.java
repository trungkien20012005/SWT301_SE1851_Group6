package com.swp.blooddonation.service;

import com.swp.blooddonation.dto.request.ScheduleRequestDTO;
import com.swp.blooddonation.dto.response.ScheduleResponseDTO;
import com.swp.blooddonation.entity.Schedule;
import com.swp.blooddonation.entity.User;
import com.swp.blooddonation.enums.ScheduleStatus;
import com.swp.blooddonation.exception.exceptions.BadRequestException;
import com.swp.blooddonation.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleServiceTest {
    @Mock
    ScheduleRepository scheduleRepository;
    @Mock
    AuthenticationService authenticationService;
    @Mock
    ModelMapper modelMapper;
    @Mock
    UserService userService;
    @InjectMocks
    ScheduleService scheduleService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testCreateSchedule_AlreadyExists_ThrowsException() {
        ScheduleRequestDTO req = new ScheduleRequestDTO();
        req.setScheduleDate(LocalDate.now());
        when(scheduleRepository.existsByScheduleDate(any())).thenReturn(true);
        assertThrows(BadRequestException.class, () -> scheduleService.createSchedule(req));
    }
} 