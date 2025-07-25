package com.swp.blooddonation;

import com.swp.blooddonation.dto.CompleteBloodTest;
import com.swp.blooddonation.dto.response.BloodTestResponse;
import com.swp.blooddonation.entity.Account;
import com.swp.blooddonation.entity.BloodTest;
import com.swp.blooddonation.entity.Register;
import com.swp.blooddonation.entity.User;
import com.swp.blooddonation.enums.RegisterStatus;
import com.swp.blooddonation.enums.Role;
import com.swp.blooddonation.repository.BloodTestRepository;
import com.swp.blooddonation.repository.RegisterRepository;
import com.swp.blooddonation.service.BloodTestService;
import com.swp.blooddonation.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BloodTestServiceTest {
    @InjectMocks
    private BloodTestService bloodTestService;
    @Mock
    private BloodTestRepository bloodTestRepository;
    @Mock
    private UserService userService;
    @Mock
    private RegisterRepository registerRepository;

    @Test
    void testCompleteBloodTest_Success() {
        Long registerId = 1L;
        CompleteBloodTest request = new CompleteBloodTest();
        BloodTest bloodTest = new BloodTest();
        Register register = new Register();
        register.setId(registerId);
        register.setStatus(RegisterStatus.APPROVED);
        // Mock userService.getCurrentUser()
        User mockUser = new User();
        Account mockAccount = new Account();
        mockAccount.setRole(Role.MEDICALSTAFF);
        mockUser.setAccount(mockAccount);
        Mockito.when(userService.getCurrentUser()).thenReturn(mockUser);
        Mockito.when(registerRepository.findById(registerId)).thenReturn(Optional.of(register));
        Mockito.when(bloodTestRepository.findByRegister(register)).thenReturn(Optional.of(bloodTest));
        Mockito.when(bloodTestRepository.save(bloodTest)).thenReturn(bloodTest);
        BloodTestResponse response = bloodTestService.completeBloodTestByRegister(registerId, request);
        Assertions.assertNotNull(response);
    }

    @Test
    void testCompleteBloodTest_Fail_NotFound() {
        Long registerId = 99L;
        CompleteBloodTest request = new CompleteBloodTest();
        Mockito.lenient().when(registerRepository.findById(registerId)).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> bloodTestService.completeBloodTestByRegister(registerId, request));
    }
}