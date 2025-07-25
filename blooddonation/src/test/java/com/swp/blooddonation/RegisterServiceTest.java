package com.swp.blooddonation;

import com.swp.blooddonation.dto.request.RegisterRequest;
import com.swp.blooddonation.dto.response.RegisterResponse;
import com.swp.blooddonation.entity.*;
import com.swp.blooddonation.enums.RegisterStatus;
import com.swp.blooddonation.repository.*;
import com.swp.blooddonation.service.NotificationService;
import com.swp.blooddonation.service.RegisterService;
import com.swp.blooddonation.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {
    @InjectMocks
    private RegisterService registerService;
    @Mock
    private RegisterRepository registerRepository;
    @Mock
    private UserService userService;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private AccountSlotRepository accountSlotRepository;
    @Mock
    private SlotRepository slotRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ScheduleRepository scheduleRepository;

    @BeforeEach
    void injectFields() {
        ReflectionTestUtils.setField(registerService, "userService", userService);
        ReflectionTestUtils.setField(registerService, "appointmentRepository", appointmentRepository);
        ReflectionTestUtils.setField(registerService, "accountSlotRepository", accountSlotRepository);
        ReflectionTestUtils.setField(registerService, "scheduleRepository", scheduleRepository);
        ReflectionTestUtils.setField(registerService, "notificationService", notificationService);
    }

    @Test
    void testCreateRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        Register register = new Register();
        Mockito.when(registerRepository.save(Mockito.any(Register.class))).thenReturn(register);
        Mockito.when(userService.getCurrentUser()).thenReturn(new User());
        Mockito.when(accountSlotRepository.existsByUserAndDate(Mockito.any(), Mockito.any())).thenReturn(false);
        Mockito.when(slotRepository.findById(Mockito.any())).thenReturn(Optional.of(new Slot()));
        Schedule schedule = new Schedule();
        schedule.setStatus(com.swp.blooddonation.enums.ScheduleStatus.OPEN);
        Mockito.when(scheduleRepository.findByScheduleDate(Mockito.any())).thenReturn(Optional.of(schedule));
        RegisterResponse response = registerService.createRegisterAndReturn(request);
        Assertions.assertNotNull(response);
    }

    @Test
    void testApproveRegister_Success() {
        Long id = 1L;
        Register register = new Register();
        register.setStatus(RegisterStatus.PENDING);
        User user = new User();
        user.setId(1L);
        register.setUser(user);
        Mockito.when(registerRepository.findById(id)).thenReturn(Optional.of(register));
        Mockito.when(registerRepository.save(register)).thenReturn(register);
        Mockito.when(appointmentRepository.save(Mockito.any())).thenReturn(new Appointment());
        Mockito.doNothing().when(notificationService).sendNotification(Mockito.any());
        Appointment appointment = registerService.approveRegister(id);
        Assertions.assertNotNull(appointment);
    }

    @Test
    void testApproveRegister_Fail_NotFound() {
        Long id = 99L;
        Mockito.when(registerRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> registerService.approveRegister(id));
    }
} 