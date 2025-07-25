package com.swp.blooddonation;

import com.swp.blooddonation.dto.request.CollectBloodRequest;
import com.swp.blooddonation.entity.*;
import com.swp.blooddonation.enums.BloodTestStatus;
import com.swp.blooddonation.enums.Role;
import com.swp.blooddonation.repository.*;
import com.swp.blooddonation.service.BloodUnitService;
import com.swp.blooddonation.service.NotificationService;
import com.swp.blooddonation.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BloodUnitServiceTest {
    @InjectMocks
    private BloodUnitService bloodUnitService;
    @Mock
    private BloodUnitRepository bloodUnitRepository;
    @Mock
    private UserService userService;
    @Mock
    private BloodTestRepository bloodTestRepository;
    @Mock
    private TestResultRepository testResultRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DonationHistoryRepository donationHistoryRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private NotificationService notificationService;

    @Test
    void testCollectBlood_Success() {
        CollectBloodRequest request = new CollectBloodRequest();
        request.setTestId(1L);
        request.setTotalVolume(350);
        BloodUnit unit = new BloodUnit();
        // Mock user
        User mockUser = new User();
        Account mockAccount = new Account();
        mockAccount.setRole(Role.MEDICALSTAFF);
        mockUser.setAccount(mockAccount);
        Mockito.when(userService.getCurrentUser()).thenReturn(mockUser);
        // Mock BloodTest
        BloodTest mockTest = new BloodTest();
        mockTest.setStatus(BloodTestStatus.COMPLETED);
        Register mockRegister = new Register();
        User donor = new User();
        mockRegister.setUser(donor);
        mockTest.setRegister(mockRegister);
        Mockito.when(bloodTestRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(mockTest));
        // Mock TestResult
        TestResult mockResult = new TestResult();
        mockResult.setPassed(true);
        Mockito.when(testResultRepository.findByBloodTest(Mockito.any())).thenReturn(java.util.Optional.of(mockResult));
        // Mock userRepository.save
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(donor);
        // Mock donationHistoryRepository.save
        Mockito.when(donationHistoryRepository.save(Mockito.any())).thenReturn(null);
        // Mock bloodUnitRepository.save
        Mockito.when(bloodUnitRepository.save(Mockito.any(BloodUnit.class))).thenReturn(unit);
        BloodUnit result = bloodUnitService.collectBlood(request);
        Assertions.assertNotNull(result);
    }

    @Test
    void testCollectBlood_Fail_InvalidRequest() {
        CollectBloodRequest request = new CollectBloodRequest();
        // Không cần mock userService.getCurrentUser() nếu không dùng tới
        Assertions.assertThrows(RuntimeException.class, () -> bloodUnitService.collectBlood(request));
    }
} 