package com.swp.blooddonation.service;

import com.swp.blooddonation.entity.Appointment;
import com.swp.blooddonation.enums.AppointmentEnum;
import com.swp.blooddonation.exception.exceptions.BadRequestException;
import com.swp.blooddonation.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;
    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testCompleteAppointment_NotFound_ThrowsException() {
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> appointmentService.completeAppointment(1L));
    }

    @Test
    void testCompleteAppointment_StatusNotScheduled_ThrowsException() {
        Appointment appt = new Appointment();
        appt.setStatus(AppointmentEnum.COMPLETED);
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appt));
        assertThrows(BadRequestException.class, () -> appointmentService.completeAppointment(1L));
    }

    @Test
    void testCountAll_ReturnsValue() {
        when(appointmentRepository.count()).thenReturn(5L);
        assertEquals(5L, appointmentService.countAll());
    }

    @Test
    void testCountByUserId_ReturnsValue() {
        when(appointmentRepository.countByCustomerId(2L)).thenReturn(3L);
        assertEquals(3L, appointmentService.countByUserId(2L));
    }
} 