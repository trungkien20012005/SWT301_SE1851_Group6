package com.swp.blooddonation;

import com.swp.blooddonation.dto.AddressDTO;
import com.swp.blooddonation.dto.request.BloodRequestRequest;
import com.swp.blooddonation.dto.request.ComponentBloodRequestRequest;
import com.swp.blooddonation.entity.*;
import com.swp.blooddonation.enums.BloodRequestStatus;
import com.swp.blooddonation.enums.BloodType;
import com.swp.blooddonation.enums.RhType;
import com.swp.blooddonation.enums.Role;
import com.swp.blooddonation.repository.*;
import com.swp.blooddonation.service.BloodRequestService;
import com.swp.blooddonation.service.NotificationService;
import com.swp.blooddonation.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BloodRequestServiceTest {
    @InjectMocks
    private BloodRequestService bloodRequestService;
    @Mock
    private WholeBloodRequestRepository wholeBloodRequestRepository;
    @Mock
    private WholeBloodRequestRepository bloodRequestRepository;
    @Mock
    private BloodRequestComponentRepository bloodRequestComponentRepository;
    @Mock
    private PendingPatientRequestRepository pendingPatientRequestRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private BloodUnitRepository bloodUnitRepository;

    private User mockUserWithRole(Role role) {
        User user = new User();
        Account account = new Account();
        account.setRole(role);
        user.setAccount(account);
        return user;
    }

    private AddressDTO mockAddress() {
        AddressDTO address = new AddressDTO();
        address.setStreet("Test Street");
        address.setWardId(1L);
        address.setDistrictId(1L);
        address.setProvinceId(1L);
        return address;
    }

    @Test
    void testRequestWholeBlood_Success() {
        BloodRequestRequest request = new BloodRequestRequest();
        request.setPatientAddress(mockAddress());
        request.setBloodType(BloodType.A);
        request.setRhType(RhType.POSITIVE);
        WholeBloodRequest wholeBloodRequest = new WholeBloodRequest();
        Mockito.lenient().when(wholeBloodRequestRepository.save(Mockito.any(WholeBloodRequest.class))).thenReturn(wholeBloodRequest);
        Mockito.when(userService.getCurrentUser()).thenReturn(mockUserWithRole(Role.CUSTOMER));
        // Nếu service gọi patientRepository.save, mock thêm:
        Mockito.lenient().when(patientRepository.save(Mockito.any())).thenReturn(new Patient());
        WholeBloodRequest result = bloodRequestService.requestBlood(request);
        Assertions.assertNotNull(result);
    }

    @Test
    void testApproveWholeBloodRequest_Success() {
        Long id = 1L;
        WholeBloodRequest wholeBloodRequest = new WholeBloodRequest();
        wholeBloodRequest.setStatus(BloodRequestStatus.PENDING);
        wholeBloodRequest.setRequiredVolume(100);

        // Thêm requester cho wholeBloodRequest
        User requester = new User();
        requester.setId(1L);
        wholeBloodRequest.setRequester(requester);

        PendingPatientRequest pending = new PendingPatientRequest();
        pending.setFullName("Test Patient");
        pending.setDateOfBirth(java.time.LocalDate.of(2000, 1, 1));
        pending.setStreet("Test Street");
        pending.setWardId(1L);
        pending.setDistrictId(1L);
        pending.setProvinceId(1L);

        Mockito.lenient().when(bloodRequestRepository.findById(id)).thenReturn(Optional.of(wholeBloodRequest));
        Mockito.lenient().when(wholeBloodRequestRepository.findById(id)).thenReturn(Optional.of(wholeBloodRequest));
        Mockito.when(pendingPatientRequestRepository.findByWholeBloodRequest(wholeBloodRequest)).thenReturn(Optional.of(pending));
        Mockito.when(patientRepository.save(Mockito.any())).thenReturn(new Patient());
        Mockito.doNothing().when(notificationService).sendNotification(Mockito.any());

        // Mock BloodUnitRepository
        BloodUnit unit = new BloodUnit();
        unit.setTotalVolume(150); // >= requiredVolume
        List<BloodUnit> units = new ArrayList<>();
        units.add(unit);
        Mockito.when(bloodUnitRepository.findUsableBloodUnits(Mockito.any(), Mockito.any())).thenReturn(units);

        bloodRequestService.approveBloodRequest(id);
        Assertions.assertTrue(true);
    }

    @Test
    void testApproveWholeBloodRequest_Fail_NotFound() {
        Long id = 99L;
        Mockito.lenient().when(wholeBloodRequestRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> bloodRequestService.approveBloodRequest(id));
    }

    @Test
    void testRequestComponentBlood_Success() {
        ComponentBloodRequestRequest request = new ComponentBloodRequestRequest();
        request.setPatientAddress(mockAddress());
        request.setBloodType(BloodType.A);
        request.setRhType(RhType.POSITIVE);
        BloodRequestComponent component = new BloodRequestComponent();
        Mockito.when(bloodRequestComponentRepository.save(Mockito.any(BloodRequestComponent.class))).thenReturn(component);
        Mockito.when(userService.getCurrentUser()).thenReturn(mockUserWithRole(Role.CUSTOMER));
        BloodRequestComponent result = bloodRequestService.requestBloodByComponent(request);
        Assertions.assertNotNull(result);
    }

    @Test
    void testApproveComponentBloodRequest_Success() {
        Long id = 2L;
        BloodRequestComponent component = new BloodRequestComponent();
        component.setStatus(BloodRequestStatus.PENDING);
        User requester = new User();
        requester.setId(1L);
        component.setRequester(requester);
        Mockito.when(bloodRequestComponentRepository.findById(id)).thenReturn(Optional.of(component));
        Mockito.when(bloodRequestComponentRepository.save(component)).thenReturn(component);
        PendingPatientRequest pending = new PendingPatientRequest();
        Mockito.when(pendingPatientRequestRepository.findByBloodRequestComponent(component)).thenReturn(Optional.of(pending));
        Mockito.when(patientRepository.save(Mockito.any())).thenReturn(new Patient());
        Mockito.doNothing().when(notificationService).sendNotification(Mockito.any());
        bloodRequestService.approveComponentRequest(id);
        Assertions.assertTrue(true);
    }

    @Test
    void testApproveComponentBloodRequest_Fail_NotFound() {
        Long id = 100L;
        Mockito.when(bloodRequestComponentRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> bloodRequestService.approveComponentRequest(id));
    }
} 